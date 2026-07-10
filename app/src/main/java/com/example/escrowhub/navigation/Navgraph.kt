package com.example.escrowhub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.escrowhub.ui.screens.HomeScreen
import com.example.escrowhub.ui.screens.NotificationScreen
import com.example.escrowhub.ui.screens.ProfileScreen
import com.example.escrowhub.ui.screens.EscrowDetailScreen
import com.example.escrowhub.ui.screens.EscrowScreen
import com.example.escrowhub.ui.screens.ForgotPasswordScreen
import com.example.escrowhub.ui.screens.LoginScreen
import com.example.escrowhub.ui.screens.RegisterScreen
import com.example.escrowhub.ui.screens.SplashScreen
import com.example.escrowhub.ui.screens.WalletScreen

@Composable
fun EscrowHubNavGraph(navController: NavHostController) {
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
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.CreateEscrow.route) {
            EscrowScreen(navController = navController)
        }
        composable(
            route = Screen.EscrowDetail.route,
            arguments = listOf(navArgument("escrowId") { type = NavType.StringType })
        ) { backStackEntry ->
            val escrowId = backStackEntry.arguments?.getString("escrowId") ?: ""
            EscrowDetailScreen(escrowId = escrowId, navController = navController)
        }
        composable(
            route = Screen.Wallet.route) {
            WalletScreen(navController = navController) 
        }
        composable(route = Screen.Notifications.route) {
            NotificationScreen(navController = navController)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
