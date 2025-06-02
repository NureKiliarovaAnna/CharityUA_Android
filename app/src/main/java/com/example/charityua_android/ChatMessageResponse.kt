package com.example.charityua_android

data class ChatMessageResponse(
    val message_id: Int,
    val sender_id: Int,
    val sender_name: String,
    val text: String,
    val created_at: String
)
