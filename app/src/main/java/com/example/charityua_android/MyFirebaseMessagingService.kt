package com.example.charityua_android

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Отримано новий FCM токен: $token")
        TokenManager.saveFcmToken(this, token)
        // Токен надсилаємо на сервер:
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val savedToken = TokenManager.getToken(this) // JWT токен користувача
        if (!savedToken.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.instance.updateFcmToken(FcmTokenRequest(token))
                    Log.d("FCM", "Токен надіслано на сервер")
                } catch (e: Exception) {
                    Log.e("FCM", "Помилка відправки FCM токена: ${e.localizedMessage}")
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val type = remoteMessage.data["type"] ?: "chat"
        val title = remoteMessage.notification?.title ?: "CharityUA"
        val body = remoteMessage.notification?.body ?: "Нове повідомлення"

        when (type) {
            "chat" -> showChatNotification(title, body)
            "fundraiser_update" -> {
                val fundraiserId = remoteMessage.data["fundraiserId"]?.toIntOrNull() ?: -1
                showFundraiserUpdateNotification(title, body, fundraiserId)
            }
            else -> showDefaultNotification(title, body)
        }
    }

    private fun showChatNotification(title: String, body: String) {
        val builder = NotificationCompat.Builder(this, "chat_notifications")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun showFundraiserUpdateNotification(title: String, body: String, fundraiserId: Int) {
        val intent = Intent(this, FundraiserDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fundraiser_id", fundraiserId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "fundraiser_updates")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun saveNotification(notification: NotificationItem) {
        val prefs = getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("notifications_list", "[]")
        val list = Gson().fromJson(json, Array<NotificationItem>::class.java).toMutableList()
        list.add(notification)
        val updatedJson = Gson().toJson(list)
        prefs.edit().putString("notifications_list", updatedJson).apply()
    }

    private fun showDefaultNotification(title: String, body: String) {
        val builder = NotificationCompat.Builder(this, "chat_notifications")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}