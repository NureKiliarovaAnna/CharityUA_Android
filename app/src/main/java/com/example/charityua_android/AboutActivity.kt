package com.example.charityua_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.charityua_android.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aboutText.text = """
            CharityUA — це платформа для збору коштів на благодійність.
            
            Ви можете:
            • Робити донати на різні збори
            • Дивитись історію своїх донатів
            • Обирати категорії зборів

            Ми гарантуємо:
            • Прозорість кожного збору
            • Перевірку організаторів
            • Надійність платежів
            
            Дякуємо за вашу підтримку!
        """.trimIndent()
    }
}