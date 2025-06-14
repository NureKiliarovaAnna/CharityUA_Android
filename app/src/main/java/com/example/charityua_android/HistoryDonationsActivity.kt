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

class HistoryDonationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationAdapter
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_donations)

        recyclerView = findViewById(R.id.donationsRecycler)
        emptyText = findViewById(R.id.empty_text)

        adapter = DonationAdapter { fundraiserId ->
            val intent = Intent(this, FundraiserDetailActivity::class.java)
            intent.putExtra("fundraiser_id", fundraiserId)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMyDonations()
                if (response.isSuccessful && response.body() != null) {
                    val donations = response.body()!!
                    if (donations.isEmpty()) {
                        emptyText.visibility = TextView.VISIBLE
                        recyclerView.visibility = RecyclerView.GONE
                    } else {
                        emptyText.visibility = TextView.GONE
                        recyclerView.visibility = RecyclerView.VISIBLE
                        adapter.submitList(donations)
                    }
                } else {
                    Toast.makeText(this@HistoryDonationsActivity, "Помилка завантаження", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistoryDonationsActivity, "Помилка з’єднання", Toast.LENGTH_SHORT).show()
            }
        }
    }
}