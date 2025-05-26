package com.example.charityua_android

data class Fundraiser(
    val fundraiser_id: Int,
    val title: String,
    val description: String,
    val goal_amount: Double,
    val collected_amount: Double,
    val status: String,
    val created_at: String
)
