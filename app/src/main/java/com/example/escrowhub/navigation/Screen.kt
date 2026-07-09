package com.example.escrowhub.navigation

sealed class Screen(val route : String){
    // Inside define screens and access paths
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object EscrowDetail : Screen("escrow_detail/{escrowId}")
    {
        fun createRoute(escrowId: String)="escrow_detail/$escrowId"
    }
    object Notification : Screen("notification")
    object Escrow : Screen("escrow")
    object Wallet : Screen("wallet")
    object TransactionHistory : Screen("transaction_history")
}