package com.example.charityua_android

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.charityua_android.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.core.view.GravityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FundraiserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Налаштування списку зборів
        adapter = FundraiserAdapter(listOf()) { fundraiser ->
            Toast.makeText(this, "Вибрано: ${fundraiser.title}", Toast.LENGTH_SHORT).show()
            // TODO: перейти на FundraiserDetailsActivity
        }

        binding.fundraisersRecycler.layoutManager = LinearLayoutManager(this)
        binding.fundraisersRecycler.adapter = adapter

        // Завантаження зборів з бекенду
        loadFundraisers()

        // Робота з drawer
        setupDrawerTriggers()
    }

    private fun setupDrawerTriggers() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawer_layout)
        val sortButton = findViewById<ImageButton>(R.id.sort_button)
        val filterBlock = findViewById<LinearLayout>(R.id.filter_block)

        sortButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // лівий drawer
        }

        filterBlock.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END) // правий drawer
        }
    }

    private fun loadFundraisers() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getFundraisers()
                if (response.isSuccessful && response.body() != null) {
                    val fundraisers = response.body()!!
                    adapter = FundraiserAdapter(fundraisers) {
                        Toast.makeText(
                            this@MainActivity,
                            "Деталі: ${it.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.fundraisersRecycler.adapter = adapter
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Помилка сервера: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@MainActivity,
                    "Проблема з мережею",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: HttpException) {
                Toast.makeText(
                    this@MainActivity,
                    "HTTP помилка: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}