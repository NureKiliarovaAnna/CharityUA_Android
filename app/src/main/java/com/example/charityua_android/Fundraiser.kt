package com.example.charityua_android

data class Fundraiser(
    val fundraiser_id: Int,
    val title: String,
    val description: String,
    val goal_amount: Int,
    val current_amount: Int,
    val status: String,
    val media_urls: List<String>,
    val created_at: String,
    val category_id: Int,
    val category_name: String,
    val organizer_name: String
)
