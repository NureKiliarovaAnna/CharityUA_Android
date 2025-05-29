package com.example.charityua_android

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HistoryDonationsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_donations)

        recyclerView = findViewById(R.id.donationsRecycler)
        adapter = DonationAdapter { fundraiserId ->
            val intent = Intent(this, FundraiserDetailActivity::class.java)
            intent.putExtra("fundraiser_id", fundraiserId)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("jwt_token", "") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(this, "Неавторизовано", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMyDonations("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    adapter.submitList(response.body())
                } else {
                    Toast.makeText(this@HistoryDonationsActivity, "Помилка завантаження", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistoryDonationsActivity, "Помилка з’єднання", Toast.LENGTH_SHORT).show()
            }
        }
    }
}