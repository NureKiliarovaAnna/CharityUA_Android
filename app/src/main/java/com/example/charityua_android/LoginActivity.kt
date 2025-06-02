package com.example.charityua_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var loginError: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        loginError = findViewById(R.id.login_error)
        forgotPassword = findViewById(R.id.forgot_password)
        registerLink = findViewById(R.id.register_link)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Заповніть всі поля")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val token = responseBody?.token ?: ""
                        val userId = responseBody?.user?.user_id ?: -1
                        val userName = response.body()?.user?.name ?: "Користувач"

                        TokenManager.saveToken(this@LoginActivity, token)
                        TokenManager.saveUserId(this@LoginActivity, userId)
                        TokenManager.saveUserName(this@LoginActivity, userName)

                        loginError.visibility = TextView.GONE
                        Toast.makeText(this@LoginActivity, "Вхід успішний", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        showError("Невірний email або пароль")
                    }
                } catch (e: HttpException) {
                    showError("HTTP помилка: ${e.code()}")
                } catch (e: IOException) {
                    showError("Немає доступу до сервера")
                } catch (e: Exception) {
                    showError("Помилка: ${e.localizedMessage}")
                }
            }
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showError(message: String) {
        loginError.text = message
        loginError.visibility = TextView.VISIBLE
    }
}