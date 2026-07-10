package com.example.escrowhub.navigation

sealed class Screen(val route : String){
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object CreateEscrow : Screen("create_escrow")
    object EscrowDetail : Screen("escrow_detail/{escrowId}")
    {
        fun createRoute(escrowId: String)="escrow_detail/$escrowId"
    }
    object Wallet : Screen("wallet")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
}
