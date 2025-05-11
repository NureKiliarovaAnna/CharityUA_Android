package com.example.charityua_android

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerLink = findViewById<TextView>(R.id.register_link)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заповніть усі поля", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: реалізуй логіку входу (через API/локально)
                Toast.makeText(this, "Успішний вхід (імітація)", Toast.LENGTH_SHORT).show()
                // Наприклад, перехід до головної сторінки:
                // startActivity(Intent(this, MainActivity::class.java))
            }
        }

        registerLink.setOnClickListener {
            // TODO: реалізуй перехід до активності реєстрації
            Toast.makeText(this, "Переходимо до реєстрації", Toast.LENGTH_SHORT).show()
        }
    }
}