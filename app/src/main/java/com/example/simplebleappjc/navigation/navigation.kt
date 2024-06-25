package com.example.simplebleappjc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.simplebleappjc.R
import com.example.simplebleappjc.model.MainViewModel
import com.example.simplebleappjc.screens.ESP32ControlScreen
import com.example.simplebleappjc.screens.ManageDeviceScreen

sealed class MyNavDestination(
    val route: String,
    val title: Int = 0,
    val label: Int = 0,
    val content: @Composable (NavController, MainViewModel) -> Unit
) {

    object ESP32Control : MyNavDestination(
        route = "ESP32Control",                          // eindeutige Kennung
        title = R.string.esp32ControlScreenTitle,        // Titel in der TopBar
        label = R.string.esp32ControlScreenLabel,        // Label in der BottomBar
        content = { navController, viewModel -> ESP32ControlScreen(navController, viewModel) }
    )

    object ManageDevice : MyNavDestination(
        route = "ManageDevice",
        title = R.string.manageDeviceScreenTitle,
        label = R.string.manageDeviceScreenLabel,
        content = { navController, viewModel -> ManageDeviceScreen(navController, viewModel) }
    )
}


val navDestinations = listOf (
    MyNavDestination.ESP32Control,
    MyNavDestination.ManageDevice
)