package com.example.charityua_android

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
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

        val title = remoteMessage.notification?.title ?: "CharityUA"
        val body = remoteMessage.notification?.body ?: "Нове повідомлення"

        val builder = NotificationCompat.Builder(this, "chat_notifications")
            .setSmallIcon(R.mipmap.ic_launcher) // Переконайся, що такий існує!
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        Log.d("FCM", "Повідомлення отримано: ${remoteMessage.data}")
    }
}