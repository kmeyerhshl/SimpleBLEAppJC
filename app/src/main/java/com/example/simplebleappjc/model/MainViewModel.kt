package com.example.simplebleappjc.model

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.example.simplebleappjc.esp32ble.Esp32Ble
import com.example.simplebleappjc.esp32ble.Esp32Data
import com.example.simplebleappjc.esp32ble.LedData
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.peripheral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

object ScanState {
    const val NOT_SCANNING = 0
    const val SCANNING = 1
    const val FAILED = 2
}

object ConnectState {
    const val NO_DEVICE = 0
    const val DEVICE_SELECTED = 1
    const val CONNECTED = 2
    const val NOT_CONNECTED = 3
}

data class Device(val name: String, val address: String) {
    override fun toString(): String = name + ": " + address
}

class MainViewModel: ViewModel() {
    private val TAG = "MainViewModel"

    private val _deviceList = MutableStateFlow<List<Device>>(mutableListOf())
    val deviceList: StateFlow<List<Device>>  get() = _deviceList

    init {
        _deviceList.value = mutableListOf()
    }

    fun getDeviceList(): List<Device>? {
        return _deviceList.value
    }

    private var deviceSelected = ""
    fun getDeviceSelected(): String {
        return deviceSelected
    }


    fun setDeviceSelected(devicestring: String) {
        deviceSelected = devicestring
        _connectState.value = ConnectState.DEVICE_SELECTED
    }

    private fun addDevice(device: Device) {
        val currentList = _deviceList.value.toMutableList()
        if (!currentList.any { it.address == device.address }) {
            currentList.add(device)
        }
        _deviceList.value = currentList.toList()
        Log.i(TAG, _deviceList.value.toString())
    }


    // Scanning
    // ------------------------------------------------------------------------------

    private lateinit var scanJob: Job
    private val scanner = Scanner()
    private var scanState = ScanState.NOT_SCANNING

    fun startScan() {
        Log.i(TAG, "startScan")
        if (scanState == ScanState.SCANNING) return // Scan already in progress.
        scanState = ScanState.SCANNING

        val SCAN_DURATION_MILLIS = TimeUnit.SECONDS.toMillis(10)
        scanJob = viewModelScope.launch {
            withTimeoutOrNull(SCAN_DURATION_MILLIS) {
                scanner
                    .advertisements
                    .catch {
                        cause -> scanState = ScanState.FAILED
                        Log.i(">>>> Scanning Failed", cause.message.toString())
                    }
                    .onCompletion { cause -> if (cause == null || cause is CancellationException)
                        scanState = ScanState.NOT_SCANNING
                    }
                    .collect { advertisement ->
                        val device = Device(name = advertisement.name.toString(),
                            address = advertisement.address.toString())
                        addDevice(device)
                        Log.i(">>>>", device.toString())
                    }
            }
        }
    }

    fun stopScan() {
        scanState = ScanState.NOT_SCANNING
        scanJob.cancel()
        Log.i(TAG, "stopScan")
    }


    // Connecting
    // --------------------------------------------------------------------------

    private lateinit var peripheral: Peripheral
    private lateinit var esp32: Esp32Ble

    private val _connectState = MutableStateFlow(ConnectState.NO_DEVICE)
    val connectState: MutableStateFlow<Int> get() = _connectState


    fun connect() {
        Log.i(TAG, "deviceSelected: $deviceSelected")
        if (_connectState.value == ConnectState.NO_DEVICE) {
            Log.d(TAG, "No device selected, returning early.")
            return
        }

        val macAddress = deviceSelected.substring(deviceSelected.length - 17)
        Log.i(TAG, "macAdress: $macAddress")
        peripheral = viewModelScope.peripheral(macAddress) {
            onServicesDiscovered {
                requestMtu(517)
            }
        }
        esp32 = Esp32Ble(peripheral)

        CoroutineScope(Dispatchers.IO).launch {
            peripheral.state.collect { state ->
                Log.i(">>>> Connection State:", state.toString())
                when (state.toString()) {
                    "Connected" -> _connectState.value = ConnectState.CONNECTED
                    "Disconnected(null)" -> _connectState.value = ConnectState.NOT_CONNECTED
                    else -> _connectState.value = ConnectState.NOT_CONNECTED
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            esp32.connect()
        }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            withTimeoutOrNull(5000L) {
                esp32.disconnect()
            }
        }
    }


    // Communication
    // ____________________________________________________________________


    var ledData = LedData()

    fun sendLedData() {
        viewModelScope.launch {
            try {
                esp32.sendMessage(jsonEncodeLedData(ledData))
            } catch (e: Exception) {
                Log.i(">>>>>", "Error sending ledData ${e.message}" + e.toString())
            }
        }
    }

    private fun jsonEncodeLedData(ledData: LedData): String {
        val obj = JSONObject()
        obj.put("LED", ledData.led)
        obj.put("LEDBlinken", ledData.ledBlinken)
        return obj.toString()
    }


    private var _esp32Data = mutableStateOf(Esp32Data())
    val esp32Data: State<Esp32Data> get() = _esp32Data

    private var dataLoadJob: Job? = null

    fun startDataLoadJob() {
        dataLoadJob = CoroutineScope(Dispatchers.IO).launch {
            esp32.incomingMessages.collect { msg ->
                val jsonString = java.lang.String(msg)
                Log.i(">>>> msg in", jsonString.toString())
                _esp32Data.value = jsonParseEsp32Data(jsonString.toString())
            }
        }
    }

    fun cancelDataLoadJob() {
        dataLoadJob?.cancel()
    }

    fun jsonParseEsp32Data(jsonString: String): Esp32Data {
        try {
            val obj = JSONObject(jsonString)
            return Esp32Data(
                ledstatus = obj.getString("ledstatus"),
                potiArray = obj.getJSONArray("potiArray")
            )
        } catch (e: Exception) {
            Log.i(">>>>", "Error decoding JSON ${e.message}")
            return Esp32Data()
        }
    }
}