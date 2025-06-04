package com.example.charityua_android

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "https://charityua.me/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val context = MyApplication.instance.applicationContext
            val token = TokenManager.getToken(context)

            // Шляхи, де токен не потрібен
            val noAuthPaths = listOf("/login", "/register", "/forgot-password", "/reset-password")
            val path = originalRequest.url.encodedPath

            val needsAuth = noAuthPaths.none { path.contains(it) }

            val modifiedRequest = if (!token.isNullOrEmpty() && needsAuth) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }

            chain.proceed(modifiedRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}