package com.example.escrowhub.model

import com.google.firebase.Timestamp

// Static values that do not change
enum class UserRole { STUDENT, TEACHER }

data class UserProfile(
    val uid: String,
    val fullname: String = "",
    val email: String,
    val role: String = "student" // default student
) {
    fun toMap(): Map<String, Any> = mapOf(
        "fullname" to fullname,
        "email" to email,
        "role" to role
    )

    fun userRole(): UserRole =
        if (role == "teacher") UserRole.TEACHER else UserRole.STUDENT
}

// model for media assets
data class MediaItem(
    val id: String="",
    val title: String="",
    val description: String="",
    val imageUrl: String="",
    val ownerName: String="",
    val ownerId: String="",
    val isPublic: String="",
    val category: String="",
    val uploadedAt: Timestamp= Timestamp.now(),

    ) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "description" to description,
        "imageUrl" to imageUrl,
        "ownerName" to ownerName,
        "is Public" to isPublic,
        "category" to category,
        "uploadedAt" to uploadedAt,
    )
}