package com.example.escrowhub.model

data class WalletTransaction(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val type: String = "DEPOSIT", // DEPOSIT, WITHDRAWAL, ESCROW_PAYMENT, ESCROW_RELEASE
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Wallet(
    val userId: String = "",
    val availableBalance: Double = 0.0,
    val lockedBalance: Double = 0.0
)
