package com.example.charityua_android

data class UserProfile(
    val user_id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatar_url: String? = null,
    val verified: Int,  // Замінено з Boolean на Int
    val provider: String
)
