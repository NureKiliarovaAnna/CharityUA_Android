package com.example.charityua_android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var registerError: TextView
    private lateinit var loginLink: TextView
    private lateinit var googleButton: ImageButton
    private lateinit var facebookButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        registerButton = findViewById(R.id.register_button)
        registerError = findViewById(R.id.register_error)
        loginLink = findViewById(R.id.login_link)
        googleButton = findViewById(R.id.google_button)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Будь ласка, заповніть усі поля")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.register(
                        RegisterRequest(name, email, password)
                    )
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Акаунт створено! Увійдіть",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        val msg = response.errorBody()?.string() ?: "Помилка реєстрації"
                        showError(msg)
                    }
                } catch (e: HttpException) {
                    showError("HTTP: ${e.code()}")
                } catch (e: IOException) {
                    showError("Немає з'єднання з сервером")
                } catch (e: Exception) {
                    showError("Помилка: ${e.localizedMessage}")
                }
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        googleButton.setOnClickListener {
            openOAuth("https://charityua.me/api/auth/google")
        }
    }

    private fun showError(message: String) {
        registerError.text = message
        registerError.visibility = TextView.VISIBLE
    }

    private fun openOAuth(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}