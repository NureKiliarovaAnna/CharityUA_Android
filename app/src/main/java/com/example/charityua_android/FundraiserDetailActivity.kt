package com.example.charityua_android

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.charityua_android.databinding.ActivityFundraiserDetailBinding
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FundraiserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFundraiserDetailBinding
    private var fundraiserId: Int = -1
    private lateinit var currentFundraiser: Fundraiser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFundraiserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.donateButton.isEnabled = false

        // 🔧 Отримуємо ID збору з Intent
        fundraiserId = intent.getIntExtra("fundraiser_id", -1)
        if (fundraiserId == -1) {
            Toast.makeText(this, "Збір не знайдено", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadFundraiser()

        binding.favoriteIconOutline.setOnClickListener {
            toggleFavorite(true)
        }

        binding.favoriteIconFilled.setOnClickListener {
            toggleFavorite(false)
        }

        binding.complaintButton.setOnClickListener {
            val fundraiser = currentFundraiser
            ComplaintBottomSheet(
                fundraiserId = fundraiser.fundraiser_id,
                onSuccess = {
                    Toast.makeText(this, "Скаргу надіслано", Toast.LENGTH_SHORT).show()
                }
            ).show(supportFragmentManager, "ComplaintBottomSheet")
        }

        binding.donateButton.setOnClickListener {
            val fundraiser = currentFundraiser
            val remainingAmount = try {
                fundraiser.goal_amount.toDouble() - fundraiser.current_amount.toDouble()
            } catch (e: Exception) {
                0.0
            }.coerceAtLeast(0.0)

            DonateBottomSheet(
                fundraiserId = fundraiser.fundraiser_id,
                maxAmount = remainingAmount,
                onSuccess = {
                    loadFundraiser()
                    Toast.makeText(this, "Збір оновлено після донату", Toast.LENGTH_SHORT).show()
                }
            ).show(supportFragmentManager, "DonateBottomSheet")
        }

        binding.chatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fundraiser_id", currentFundraiser.fundraiser_id)
            intent.putExtra("fundraiser_name", currentFundraiser.title)
            startActivity(intent)
        }

        binding.facebookShare.setOnClickListener {
            shareFundraiser("facebook")
        }

        binding.instagramShare.setOnClickListener {
            shareFundraiser("instagram")
        }

        binding.telegramShare.setOnClickListener {
            shareFundraiser("telegram")
        }
    }

    private fun loadFundraiser() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getFundraiserById(fundraiserId)
                if (response.isSuccessful && response.body() != null) {
                    currentFundraiser = response.body()!!
                    bindData(currentFundraiser)
                    checkIfFavorite()
                } else {
                    Toast.makeText(this@FundraiserDetailActivity, "Збір не знайдено", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FundraiserDetailActivity, "Помилка підключення", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun bindData(f: Fundraiser) {
        binding.fundraiserTitle.text = f.title
        binding.fundraiserCurrentAmount.text = "${f.current_amount} грн"
        binding.fundraiserGoalAmount.text = "${f.goal_amount} грн"
        binding.organizerName.text = f.organizer_name
        binding.fundraiserDescription.text = f.description

        val adapter = ImageSliderAdapter(f.media_urls)
        binding.fundraiserImagesPager.adapter = adapter
        binding.donateButton.isEnabled = true
    }

    private fun checkIfFavorite() {
        lifecycleScope.launch {
            try {
                val favoritesResponse = RetrofitClient.instance.getFavorites()
                if (favoritesResponse.isSuccessful && favoritesResponse.body() != null) {
                    val favorites = favoritesResponse.body()!!
                    val isFavorite = favorites.any { it.fundraiser_id == fundraiserId }
                    if (isFavorite) {
                        binding.favoriteIconOutline.visibility = View.GONE
                        binding.favoriteIconFilled.visibility = View.VISIBLE
                    } else {
                        binding.favoriteIconOutline.visibility = View.VISIBLE
                        binding.favoriteIconFilled.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@FundraiserDetailActivity, "Помилка перевірки обраного", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleFavorite(toFavorite: Boolean) {
        val token = TokenManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Потрібно увійти", Toast.LENGTH_SHORT).show()
            return
        }

        if (toFavorite) {
            binding.favoriteIconOutline.visibility = View.GONE
            binding.favoriteIconFilled.visibility = View.VISIBLE

            val request = FavoriteRequest(fundraiser_id = currentFundraiser.fundraiser_id)
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.addFavorite(request)
                    if (response.isSuccessful) {
                        Toast.makeText(this@FundraiserDetailActivity, "Додано в обране", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@FundraiserDetailActivity, "Не вдалося додати", Toast.LENGTH_SHORT).show()
                        binding.favoriteIconOutline.visibility = View.VISIBLE
                        binding.favoriteIconFilled.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@FundraiserDetailActivity, "Помилка з’єднання", Toast.LENGTH_SHORT).show()
                    binding.favoriteIconOutline.visibility = View.VISIBLE
                    binding.favoriteIconFilled.visibility = View.GONE
                }
            }
        } else {
            binding.favoriteIconOutline.visibility = View.VISIBLE
            binding.favoriteIconFilled.visibility = View.GONE

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.removeFavorite(currentFundraiser.fundraiser_id)
                    if (response.isSuccessful) {
                        Toast.makeText(this@FundraiserDetailActivity, "Видалено з обраного", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@FundraiserDetailActivity, "Не вдалося видалити", Toast.LENGTH_SHORT).show()
                        binding.favoriteIconOutline.visibility = View.GONE
                        binding.favoriteIconFilled.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@FundraiserDetailActivity, "Помилка з’єднання", Toast.LENGTH_SHORT).show()
                    binding.favoriteIconOutline.visibility = View.GONE
                    binding.favoriteIconFilled.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun shareFundraiser(platform: String) {
        val shareText = "Долучайтесь до збору: ${currentFundraiser.title}\n\n" +
                "Деталі: https://your-website.com/fundraiser/${currentFundraiser.fundraiser_id}"

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        // Для Telegram можна спробувати відкривати офіційний застосунок
        if (platform == "telegram") {
            intent.setPackage("org.telegram.messenger")
        } else if (platform == "facebook") {
            intent.setPackage("com.facebook.katana")
        } else if (platform == "instagram") {
            intent.setPackage("com.instagram.android")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Немає додатку $platform на вашому телефоні", Toast.LENGTH_SHORT).show()
        }
    }
}