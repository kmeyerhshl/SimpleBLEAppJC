package com.example.simplebleappjc.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.simplebleappjc.model.MainViewModel
import com.example.simplebleappjc.navigation.MyNavDestination
import com.example.simplebleappjc.navigation.MyTopBar
import com.example.simplebleappjc.navigation.navDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            MyTopBar(
                navController = navController,
                screens = navDestinations,
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MyNavDestination.ESP32Control.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // Screens via BottomBar und Fullscreens
            navDestinations.forEach { screen ->
                composable(screen.route) {
                    screen.content(navController, viewModel)
                }
            }
        }
    }
}