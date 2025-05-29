package com.example.charityua_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.charityua_android.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.accountButton.setOnClickListener {
            //startActivity(Intent(this, AccountActivity::class.java))
        }

        binding.donationsButton.setOnClickListener {
            //startActivity(Intent(this, DonationHistoryActivity::class.java))
        }

        binding.favoritesButton.setOnClickListener {
            //startActivity(Intent(this, FavoriteFundraisersActivity::class.java))
        }

        binding.notificationsButton.setOnClickListener {
            //startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.chatsButton.setOnClickListener {
            //startActivity(Intent(this, OrganizerChatsActivity::class.java))
        }

        binding.supportButton.setOnClickListener {
            //startActivity(Intent(this, SupportActivity::class.java))
        }
    }
}