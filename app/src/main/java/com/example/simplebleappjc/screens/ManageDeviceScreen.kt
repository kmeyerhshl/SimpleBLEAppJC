package com.example.simplebleappjc.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
fun ManageDeviceScreen (
    navController: NavController,
    viewModel: MainViewModel
) {
    var devices by remember { mutableStateOf(listOf("Device 1", "Device 2", "Device 3")) }
    var isSearching by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            items(devices) { device ->
                Text(text = device, modifier = Modifier.padding(8.dp))
            }
        }

        Button(
            onClick = {
                // Start searching for devices
                isSearching = true
                // Simulate device search
                devices = listOf("Device A", "Device B", "Device C")
                isSearching = false
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 50.dp)
        ) {
            Text(text = "Start Search Devices")
        }

        if (isSearching) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}