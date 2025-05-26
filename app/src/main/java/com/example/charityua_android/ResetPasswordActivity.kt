package com.example.charityua_android

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var newPasswordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var resetButton: Button
    private lateinit var statusText: TextView
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        newPasswordInput = findViewById(R.id.new_password_input)
        confirmPasswordInput = findViewById(R.id.confirm_password_input)
        resetButton = findViewById(R.id.reset_button)
        statusText = findViewById(R.id.status_text)

        token = intent?.data?.getQueryParameter("token")
            ?: intent?.getStringExtra("token")
                    ?: ""

        if (token.isEmpty()) {
            showMessage("❌ Невірне або відсутнє посилання", true)
            resetButton.isEnabled = false
        }

        resetButton.setOnClickListener {
            val password = newPasswordInput.text.toString().trim()
            val confirm = confirmPasswordInput.text.toString().trim()

            if (password.length < 6) {
                showMessage("Пароль має містити мінімум 6 символів", true)
                return@setOnClickListener
            }

            if (password != confirm) {
                showMessage("Паролі не збігаються", true)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.resetPassword(
                        ResetPasswordRequest(token, password)
                    )
                    if (response.isSuccessful) {
                        showMessage("✅ Пароль змінено! Тепер увійдіть")
                        resetButton.isEnabled = false
                    } else {
                        showMessage("❌ Не вдалося змінити пароль", true)
                    }
                } catch (e: IOException) {
                    showMessage("❌ Проблема з мережею", true)
                } catch (e: HttpException) {
                    showMessage("❌ Серверна помилка", true)
                } catch (e: Exception) {
                    showMessage("❌ ${e.localizedMessage}", true)
                }
            }
        }
    }

    private fun showMessage(message: String, isError: Boolean = false) {
        statusText.text = message
        statusText.visibility = TextView.VISIBLE
        statusText.setTextColor(if (isError) 0xFFB00020.toInt() else 0xFF007700.toInt())
    }
}