package com.example.charityua_android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.charityua_android.databinding.ActivityProfileEditBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import android.util.Patterns

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private var avatarUri: Uri? = null
    private var currentUser: UserProfile? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            avatarUri = it
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.avatarImageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val avatarUrl = intent.getStringExtra("avatar_url")

        binding.nameEditText.setText(name)
        binding.emailEditText.setText(email)

        if (!avatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .circleCrop()
                .into(binding.avatarImageView)
        } else {
            binding.avatarImageView.setImageResource(R.drawable.avatar_placeholder)
        }

        loadProfile()

        binding.changeAvatarButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val token = TokenManager.getToken(this@ProfileEditActivity) ?: return@launch
                val response = RetrofitClient.instance.getProfile("Bearer $token")
                if (response.isSuccessful) {
                    val user = response.body()!!
                    currentUser = user

                    binding.nameEditText.setText(user.name)
                    binding.emailEditText.setText(user.email)

                    Glide.with(this@ProfileEditActivity)
                        .load(user.avatar_url)
                        .placeholder(R.drawable.avatar_placeholder)
                        .circleCrop()
                        .into(binding.avatarImageView)

                    val isLocal = user.provider == "local"
                    binding.emailEditText.isEnabled = isLocal
                    binding.passwordEditText.isEnabled = isLocal
                    if (!isLocal) {
                        binding.emailEditText.hint = "Email (недоступний)"
                        binding.passwordEditText.hint = "Пароль (недоступний)"
                    }
                } else {
                    Toast.makeText(this@ProfileEditActivity, "Помилка завантаження профілю", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileEditActivity, "Помилка з'єднання", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfile() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Ім’я обов’язкове", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser?.provider == "local" && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некоректний email", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val token = TokenManager.getToken(this@ProfileEditActivity) ?: return@launch

                val userPart = HashMap<String, String>()
                userPart["name"] = name
                if (currentUser?.provider == "local") {
                    userPart["email"] = email
                    if (password.isNotEmpty()) userPart["password"] = password
                }

                val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                for ((k, v) in userPart) body.addFormDataPart(k, v)

                avatarUri?.let {
                    val file = FileUtils.getFileFromUri(this@ProfileEditActivity, it)
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    body.addFormDataPart("avatar", file.name, requestBody)
                }

                val response = RetrofitClient.instance.updateProfile(
                    "Bearer $token",
                    body.build()
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileEditActivity, "Профіль оновлено", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileEditActivity, ProfileActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ProfileEditActivity, "Не вдалося зберегти", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileEditActivity, "Помилка з'єднання", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
