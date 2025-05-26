package com.example.charityua_android

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class TestConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "⏳ Перевірка з'єднання..."
            textSize = 18f
            setPadding(30, 100, 30, 30)
        }

        setContentView(textView)

        lifecycleScope.launch {
            Log.d("TestConnection", "🚀 Старт запиту...")
            try {
                val response = RetrofitClient.instance.getCategories()
                Log.d("TestConnection", "📥 Отримано відповідь: $response")
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    Log.d("TestConnection", "✅ Категорії: $categories")

                    textView.text = "✅ Успішно!\n\nКатегорії:\n" +
                            categories.joinToString("\n") { "- ${it.name}" }
                } else {
                    Log.e("TestConnection", "❌ Сервер відповів з кодом ${response.code()}")
                    textView.text = "❌ ${response.code()} — ${response.message()}"
                }
            } catch (e: IOException) {
                Log.e("TestConnection", "❌ IOException", e)
                textView.text = "❌ Проблема з мережею: ${e.localizedMessage}"
            } catch (e: HttpException) {
                Log.e("TestConnection", "❌ HttpException", e)
                textView.text = "❌ HTTP помилка: ${e.code()} ${e.message()}"
            } catch (e: Exception) {
                Log.e("TestConnection", "❌ Інша помилка", e)
                textView.text = "❌ Виняток: ${e.localizedMessage}"
            }
        }
    }
}
