package com.example.escrowhub.model

// Static values that do not change
enum class UserRole { SELLER, BUYER }

data class UserProfile(
    val uid: String,
    val fullname: String = "",
    val email: String,
    val role: String = "buyer" // default student
) {
    fun toMap(): Map<String, Any> = mapOf(
        "fullname" to fullname,
        "email" to email,
        "role" to role
    )

    fun userRole(): UserRole =
        if (role == "buyer") UserRole.BUYER else UserRole.SELLER
}

// model for Escrow transactions
data class Escrow(
    val id: String="",  // Unique Id for transactions
    val buyerId: String="",  // The person paying
    val sellerId: String="",   // The UID of the seller (if known)
    val sellerEmail: String="", // Seller's contact email
    val sellerPhone: String="", // Seller's contact phone
    val itemName: String="",   // The good being paid for(e.g. "Iphone 16")
    val description: String="",  // Detailed description of the item
    val amount: Double=0.0,     // Price to be paid
    val status: String="PENDING",   // PENDING, DISPATCHED, COMPLETED
    val createdAt: Long = System.currentTimeMillis(),
)