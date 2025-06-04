package com.example.charityua_android

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.charityua_android.databinding.ActivityProfileEditBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private var avatarUri: Uri? = null
    private var currentUser: UserProfile? = null

    private val pickImageLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
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

        // Завантаження даних профілю
        loadProfile()

        binding.changeAvatarButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
        }

        binding.logoutButton.setOnClickListener {
            confirmLogout()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val token = TokenManager.getToken(this@ProfileEditActivity) ?: return@launch
                val response = RetrofitClient.instance.getProfile()
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
                    binding.currentPasswordEditText.isEnabled = isLocal
                    binding.newPasswordEditText.isEnabled = isLocal

                    if (!isLocal) {
                        binding.emailEditText.hint = "Email (недоступний)"
                        binding.currentPasswordEditText.hint = "Поточний пароль (недоступний)"
                        binding.newPasswordEditText.hint = "Новий пароль (недоступний)"
                    }
                } else {
                    Toast.makeText(
                        this@ProfileEditActivity,
                        "Не вдалося завантажити профіль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileEditActivity, "Помилка з'єднання", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveProfile() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val currentPassword = binding.currentPasswordEditText.text.toString().trim()
        val newPassword = binding.newPasswordEditText.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Ім’я обов’язкове", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser?.provider == "local" && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некоректний email", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isNotEmpty() && currentPassword.isEmpty()) {
            Toast.makeText(this, "Щоб змінити пароль, введіть поточний", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val token = TokenManager.getToken(this@ProfileEditActivity) ?: return@launch

                val parts = mutableListOf<MultipartBody.Part>()
                parts.add(MultipartBody.Part.createFormData("name", name))
                if (currentUser?.provider == "local") {
                    parts.add(MultipartBody.Part.createFormData("email", email))
                    if (newPassword.isNotEmpty()) {
                        parts.add(
                            MultipartBody.Part.createFormData(
                                "current_password",
                                currentPassword
                            )
                        )
                        parts.add(
                            MultipartBody.Part.createFormData(
                                "new_password",
                                newPassword
                            )
                        )
                    }
                }

                avatarUri?.let {
                    val file = FileUtils.getFileFromUri(this@ProfileEditActivity, it)
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    parts.add(
                        MultipartBody.Part.createFormData(
                            "avatar",
                            file.name,
                            requestBody
                        )
                    )
                }

                val response = RetrofitClient.instance.updateProfile(
                    "Bearer $token",
                    parts
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ProfileEditActivity,
                        "Профіль оновлено",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@ProfileEditActivity, ProfileActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@ProfileEditActivity,
                        "Не вдалося зберегти",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileEditActivity, "Помилка з'єднання", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Вихід")
            .setMessage("Ви дійсно хочете вийти з акаунту?")
            .setPositiveButton("Так") { _, _ ->
                TokenManager.clearToken(this)
                TokenManager.clearUserId(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
            .setNegativeButton("Ні", null)
            .show()
    }
}