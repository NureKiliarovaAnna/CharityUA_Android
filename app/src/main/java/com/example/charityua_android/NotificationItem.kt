package com.example.charityua_android

data class NotificationItem(
    val id: Long,
    val title: String,
    val message: String,
    val timestamp: Long,
    var isRead: Boolean = false
)
