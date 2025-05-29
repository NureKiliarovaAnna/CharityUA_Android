package com.example.charityua_android

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var avatar: ImageView
    private lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        avatar = findViewById(R.id.avatar)
        userName = findViewById(R.id.user_name)

        // Завантажити профіль
        loadProfile()

        // Кнопки профілю
        findViewById<Button>(R.id.account_button).setOnClickListener {
            Toast.makeText(this, "Редагування профілю ще не реалізовано", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.donations_button).setOnClickListener {
            startActivity(Intent(this, HistoryDonationsActivity::class.java))
        }

        findViewById<Button>(R.id.favorites_button).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        findViewById<Button>(R.id.notifications_button).setOnClickListener {
            Toast.makeText(this, "Сповіщення ще не реалізовані", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.chats_button).setOnClickListener {
            Toast.makeText(this, "Чати ще не реалізовані", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.support_button).setOnClickListener {
            Toast.makeText(this, "Служба підтримки ще не реалізована", Toast.LENGTH_SHORT).show()
        }

        // Нижнє меню
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            // Вже тут — нічого не робимо
        }
    }

    private fun loadProfile() {
        val token = TokenManager.getToken(this)
        if (token.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProfile("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    userName.text = profile.name

                    if (!profile.avatar_url.isNullOrEmpty()) {
                        Glide.with(this@ProfileActivity)
                            .load(profile.avatar_url)
                            .into(avatar)
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Не вдалося завантажити профіль", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Помилка з’єднання: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}