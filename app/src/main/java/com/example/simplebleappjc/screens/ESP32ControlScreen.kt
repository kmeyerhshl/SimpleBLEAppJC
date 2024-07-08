package com.example.simplebleappjc.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.simplebleappjc.model.ConnectState
import com.example.simplebleappjc.model.MainViewModel
import com.example.simplebleappjc.navigation.MyNavDestination

@Composable
fun ESP32ControlScreen (
    navController: NavController,
    viewModel: MainViewModel
){
    var selectedDevice = viewModel.getDeviceSelected()
    var dataVisibility by remember { mutableStateOf(false) }
    val connectState by viewModel.connectState.collectAsState()
    val esp32Data = viewModel.esp32Data.value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (selectedDevice.isEmpty()) "No selected device" else selectedDevice,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = when (connectState) {
                ConnectState.CONNECTED -> "connected"
                ConnectState.NOT_CONNECTED -> "not connected"
                ConnectState.NO_DEVICE -> "no selected device"
                ConnectState.DEVICE_SELECTED -> "connecting"
                else -> {
                    "no selected device"
                }
            },
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (dataVisibility) {
            Text(
                text = "LED Status: ${esp32Data?.ledstatus ?: "N/A"}",
                fontSize = 20.sp
            )
            Text(
                text = "Potentiometer Array: ${esp32Data.potiArray ?: "N/A"}",
                fontSize = 20.sp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SwitchControl("Data Switch") { isChecked ->
                if (isChecked) {
                    viewModel.startDataLoadJob()
                    dataVisibility = true
                } else {
                    viewModel.cancelDataLoadJob()
                }
            }
            SwitchControl("LED Switch") { isChecked ->
                if (isChecked) viewModel.ledData.led = "H"
                else viewModel.ledData.led = "L"
                viewModel.sendLedData()
            }
            SwitchControl("Blink Switch") { isChecked ->
                if (isChecked) viewModel.ledData.ledBlinken = true
                else viewModel.ledData.ledBlinken = false
                viewModel.sendLedData()
            }
        }

        Spacer(modifier = Modifier.height(300.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.connect() },
                modifier = Modifier.weight(1f),
                enabled = when (connectState) {
                    ConnectState.CONNECTED -> false
                    ConnectState.NOT_CONNECTED -> true
                    ConnectState.NO_DEVICE -> false
                    ConnectState.DEVICE_SELECTED -> true
                    else -> false
                }
            ) {
                Text("Connect")
            }

            Button(
                onClick = { viewModel.disconnect() },
                modifier = Modifier.weight(1f),
                enabled = when (connectState) {
                    ConnectState.CONNECTED -> true
                    ConnectState.NOT_CONNECTED -> false
                    ConnectState.NO_DEVICE -> false
                    ConnectState.DEVICE_SELECTED -> false
                    else -> false
                }
            ) {
                Text("Disconnect")
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(MyNavDestination.ManageDevice.route)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Select Device")
        }
    }
}

@Composable
fun SwitchControl(label: String, onCheckedChange: (Boolean) -> Unit) {
    var checkedState by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(
            checked = checkedState,
            onCheckedChange = { checked ->
                checkedState = checked
                onCheckedChange(checked)
            }
        )
    }
}