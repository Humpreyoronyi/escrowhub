package com.example.escrowhub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.escrowhub.ui.screens.DashboardScreen
import com.example.escrowhub.ui.screens.EscrowScreen
import com.example.escrowhub.ui.screens.LoginScreen
import com.example.escrowhub.ui.screens.SplashScreen

@Composable
fun MediaHubNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(route = Screen.Escrow.route) {
            EscrowScreen(navController = navController)
        }
    }
}
