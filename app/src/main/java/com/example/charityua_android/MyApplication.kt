package com.example.charityua_android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chatChannel = NotificationChannel(
                "chat_notifications",
                "Чати",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Сповіщення для чатів"
            }

            val updateChannel = NotificationChannel(
                "fundraiser_updates",
                "Оновлення зборів",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Сповіщення про оновлення зборів"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(chatChannel)
            notificationManager.createNotificationChannel(updateChannel)
        }
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}