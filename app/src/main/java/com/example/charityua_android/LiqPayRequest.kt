package com.example.charityua_android

data class LiqPayRequest(
    val amount: Double,
    val currency: String,
    val description: String,
    val order_id: String
)
