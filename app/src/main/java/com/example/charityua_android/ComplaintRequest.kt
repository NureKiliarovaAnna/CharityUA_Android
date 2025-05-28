package com.example.charityua_android

data class ComplaintRequest(
    val fundraiser_id: Int,
    val reason: String? = null
)
