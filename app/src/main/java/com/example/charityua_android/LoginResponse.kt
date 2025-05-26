package com.example.charityua_android

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserProfile
)
