package com.example.charityua_android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        recyclerView = findViewById(R.id.notificationsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val notifications = getNotifications()
        notifications.forEach { it.isRead = true } // відзначаємо як прочитані
        saveNotifications(notifications)

        adapter = NotificationsAdapter(notifications)
        recyclerView.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Сповіщення"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun getNotifications(): MutableList<NotificationItem> {
        val prefs = getSharedPreferences("notifications_prefs", MODE_PRIVATE)
        val json = prefs.getString("notifications_list", "[]")
        val type = object : TypeToken<MutableList<NotificationItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveNotifications(list: List<NotificationItem>) {
        val json = Gson().toJson(list)
        val prefs = getSharedPreferences("notifications_prefs", MODE_PRIVATE)
        prefs.edit().putString("notifications_list", json).apply()
    }
}