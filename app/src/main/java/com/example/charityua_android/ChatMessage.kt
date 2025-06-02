package com.example.charityua_android

data class ChatMessage(
    val senderName: String,
    val text: String,
    val time: String,
    val isFromOrganizer: Boolean,
    val isDateHeader: Boolean = false // якщо true — це дата
)
