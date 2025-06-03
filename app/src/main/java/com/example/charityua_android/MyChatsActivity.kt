package com.example.charityua_android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MyChatsActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatListAdapter
    private val chats = mutableListOf<ChatSummary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_chats)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatAdapter = ChatListAdapter(chats) { chat ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fundraiser_id", chat.fundraiserId)
            intent.putExtra("fundraiser_name", chat.fundraiserTitle)
            intent.putExtra("chat_id", chat.chatId)
            startActivity(intent)
        }

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        loadChats()
    }

    override fun onResume() {
        super.onResume()
        loadChats()
    }

    private fun loadChats() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMyChats()
                if (response.isSuccessful && response.body() != null) {
                    chats.clear()
                    chats.addAll(response.body()!!)
                    chatAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MyChatsActivity, "Не вдалося завантажити чати", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@MyChatsActivity, "Проблема з мережею", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(this@MyChatsActivity, "HTTP помилка", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MyChatsActivity, "Помилка: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}