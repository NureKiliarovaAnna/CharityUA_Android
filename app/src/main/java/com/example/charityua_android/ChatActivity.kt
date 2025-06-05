package com.example.charityua_android

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var fundraiserTitle: TextView

    private val messages = mutableListOf<ChatMessage>()
    private var chatId: Int = -1
    private var fundraiserId: Int = -1
    private var fundraiserName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        fundraiserTitle = findViewById(R.id.chatFundraiserTitle)

        // Отримуємо дані з Intent
        fundraiserId = intent.getIntExtra("fundraiser_id", -1)
        fundraiserName = intent.getStringExtra("fundraiser_name") ?: "Збір"

        fundraiserTitle.text = fundraiserName

        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        createOrLoadChat()

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty() && chatId != -1) {
                sendMessage(messageText)
                messageEditText.text.clear()
            }
        }

        val chatId = intent.getIntExtra("chat_id", -1)
        if (chatId != -1) {
            markMessagesAsRead(chatId)
        }
    }

    private fun createOrLoadChat() {
        lifecycleScope.launch {
            try {
                val request = CreateChatRequest(fundraiser_id = fundraiserId)
                val response = RetrofitClient.instance.createChat(request)
                if (response.isSuccessful && response.body() != null) {
                    chatId = response.body()!!.chat_id
                    loadMessages()
                } else {
                    Toast.makeText(
                        this@ChatActivity,
                        "Не вдалося створити чат",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@ChatActivity, "Проблема з мережею", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(this@ChatActivity, "HTTP помилка", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, "Помилка: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChatMessages(chatId)
                if (response.isSuccessful && response.body() != null) {
                    messages.clear()
                    val loadedMessages = response.body()!!
                    val currentUserId = TokenManager.getUserId(this@ChatActivity)

                    var lastDate = ""
                    for (msg in loadedMessages) {
                        val msgDate = formatDate(msg.created_at)
                        if (msgDate != lastDate) {
                            messages.add(
                                ChatMessage(
                                    senderName = "",
                                    text = msgDate,
                                    time = "",
                                    isFromOrganizer = false,
                                    isDateHeader = true
                                )
                            )
                            lastDate = msgDate
                        }

                        val isFromOrganizer = msg.sender_id != currentUserId
                        messages.add(
                            ChatMessage(
                                senderName = msg.sender_name,
                                text = msg.text,
                                time = formatTime(msg.created_at), // лише час
                                isFromOrganizer = isFromOrganizer
                            )
                        )
                    }

                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messages.size - 1)
                } else {
                    Toast.makeText(
                        this@ChatActivity,
                        "Не вдалося завантажити повідомлення",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, "Помилка: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDate(datetime: String): String {
        // Очікуємо: "2025-06-01 14:23:45"
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val date = inputFormat.parse(datetime)
            val outputFormat = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("uk"))
            outputFormat.format(date!!)
        } catch (e: Exception) {
            datetime.take(10)
        }
    }

    private fun sendMessage(messageText: String) {
        lifecycleScope.launch {
            try {
                val request = SendMessageRequest(text = messageText)
                val response = RetrofitClient.instance.sendMessage(chatId, request)
                if (response.isSuccessful) {
                    // Додаємо повідомлення локально
                    val currentUserName = TokenManager.getUserName(this@ChatActivity)
                    messages.add(
                        ChatMessage(
                            senderName = currentUserName,
                            text = messageText,
                            time = getCurrentTimeFormatted(),
                            isFromOrganizer = false
                        )
                    )
                    chatAdapter.notifyItemInserted(messages.size - 1)
                    chatRecyclerView.scrollToPosition(messages.size - 1)
                } else {
                    Toast.makeText(
                        this@ChatActivity,
                        "Не вдалося надіслати повідомлення",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@ChatActivity, "Проблема з мережею", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(this@ChatActivity, "HTTP помилка", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, "Помилка: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentTimeFormatted(): String {
        val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return now.format(java.util.Date())
    }

    private fun formatTime(datetime: String): String {
        // Очікуємо: "2025-06-01T14:23:45.000Z" або "2025-06-01 14:23:45"
        return try {
            val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = isoFormat.parse(datetime)

            // Додаємо зміщення на -3 години
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date!!
            calendar.add(java.util.Calendar.HOUR_OF_DAY, -3)

            val outputFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            // fallback для рядків типу "2025-06-01 14:23:45"
            try {
                val fallbackFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val date = fallbackFormat.parse(datetime)

                // Додаємо зміщення на -3 години
                val calendar = java.util.Calendar.getInstance()
                calendar.time = date!!
                calendar.add(java.util.Calendar.HOUR_OF_DAY, -3)

                val outputFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                outputFormat.format(calendar.time)
            } catch (e2: Exception) {
                datetime.takeLast(5) // fallback на останні символи
            }
        }
    }

    private fun markMessagesAsRead(chatId: Int) {
        lifecycleScope.launch {
            try {
                RetrofitClient.instance.markMessagesAsRead(chatId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}