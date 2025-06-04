package com.example.charityua_android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private lateinit var googleButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        loginError = findViewById(R.id.login_error)
        forgotPassword = findViewById(R.id.forgot_password)
        registerLink = findViewById(R.id.register_link)
        googleButton = findViewById(R.id.google_button)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Заповніть всі поля")
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        googleButton.setOnClickListener {
            openOAuth("https://charityua.me/api/auth/google")
        }

        // Обробляємо intent на випадок Google OAuth
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.isHierarchical) {
            val token = data.getQueryParameter("token")
            if (!token.isNullOrEmpty()) {
                TokenManager.saveToken(this, token)
                Toast.makeText(this, "Вхід через Google успішний!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val token = responseBody?.token ?: ""
                    val userId = responseBody?.user?.user_id ?: -1
                    val userName = responseBody?.user?.name ?: "Користувач"

                    TokenManager.saveToken(this@LoginActivity, token)
                    TokenManager.saveUserId(this@LoginActivity, userId)
                    TokenManager.saveUserName(this@LoginActivity, userName)

                    val fcmToken = TokenManager.getFcmToken(this@LoginActivity)
                    if (!fcmToken.isNullOrEmpty()) {
                        lifecycleScope.launch {
                            try {
                                RetrofitClient.instance.updateFcmToken(FcmTokenRequest(fcmToken))
                            } catch (e: Exception) {
                                Log.e("FCM", "Помилка відправки токена: ${e.localizedMessage}")
                            }
                        }
                    }

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

    private fun showError(message: String) {
        loginError.text = message
        loginError.visibility = TextView.VISIBLE
    }

    private fun openOAuth(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
