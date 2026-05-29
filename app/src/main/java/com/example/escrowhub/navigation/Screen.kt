package com.example.escrowhub.navigation

sealed class Screen(val route : String){
    // Inside define screens and access paths
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object MediaDetail : Screen("media_detail/{mediaId}")
    {
        fun createRoute(mediaId: String)="media_detail/$mediaId"
    }
    object Notification : Screen("notification")
    object Escrow : Screen("escrow")
    object TransactionHistory : Screen("transaction_history")
}