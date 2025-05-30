package com.example.charityua_android

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteFundraiserAdapter
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecycler)
        emptyText = findViewById(R.id.empty_text)

        adapter = FavoriteFundraiserAdapter(listOf()) { fundraiserId ->
            val intent = Intent(this, FundraiserDetailActivity::class.java)
            intent.putExtra("fundraiser_id", fundraiserId)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getFavorites()
                if (response.isSuccessful && response.body() != null) {
                    val favorites = response.body()!!
                    if (favorites.isEmpty()) {
                        emptyText.visibility = TextView.VISIBLE
                        recyclerView.visibility = RecyclerView.GONE
                    } else {
                        emptyText.visibility = TextView.GONE
                        recyclerView.visibility = RecyclerView.VISIBLE
                        adapter = FavoriteFundraiserAdapter(favorites) { fundraiserId ->
                            val intent = Intent(this@FavoritesActivity, FundraiserDetailActivity::class.java)
                            intent.putExtra("fundraiser_id", fundraiserId)
                            startActivity(intent)
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@FavoritesActivity, "Помилка завантаження", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FavoritesActivity, "Помилка з’єднання", Toast.LENGTH_SHORT).show()
            }
        }
    }
}