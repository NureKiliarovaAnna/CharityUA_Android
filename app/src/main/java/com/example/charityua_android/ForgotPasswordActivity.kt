package com.example.charityua_android

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var sendButton: Button
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailInput = findViewById(R.id.email_input)
        sendButton = findViewById(R.id.send_button)
        statusText = findViewById(R.id.status_text)

        sendButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                showMessage("Введіть ел. пошту", isError = true)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.forgotPassword(ForgotPasswordRequest(email))
                    if (response.isSuccessful) {
                        showMessage("📧 Лист надіслано! Перевірте пошту")
                    } else {
                        showMessage("❌ Не вдалося надіслати лист", isError = true)
                    }
                } catch (e: IOException) {
                    showMessage("❌ Проблема з мережею", isError = true)
                } catch (e: HttpException) {
                    showMessage("❌ Серверна помилка", isError = true)
                } catch (e: Exception) {
                    showMessage("❌ ${e.localizedMessage}", isError = true)
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