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
import com.example.simplebleappjc.model.MainViewModel
import com.example.simplebleappjc.navigation.MyNavDestination

@Composable
fun ESP32ControlScreen (
    navController: NavController,
    viewModel: MainViewModel
){
    var selectedDevice by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    var dataVisibility by remember { mutableStateOf(false) }

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
            text = if (isConnected) "Connected" else "Not Connected",
            fontSize = 18.sp
        )

        if (dataVisibility) {
            Text(
                text = "Some data here",
                fontSize = 20.sp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SwitchControl("Data Switch") { dataVisibility = it }
            SwitchControl("LED Switch") { /* handle LED switch */ }
            SwitchControl("Blink Switch") { /* handle blink switch */ }
        }

        Spacer(modifier = Modifier.height(300.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isConnected = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Connect")
            }

            Button(
                onClick = { isConnected = false },
                modifier = Modifier.weight(1f)
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