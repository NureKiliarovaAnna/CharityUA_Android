package com.example.charityua_android
import com.google.gson.annotations.SerializedName

data class ChatSummary(
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("fundraiser_id")
    val fundraiserId: Int,
    @SerializedName("fundraiser_title")
    val fundraiserTitle: String,
    @SerializedName("last_message")
    val lastMessage: String,
    @SerializedName("unread_count")
    val unreadCount: Int
)