package com.example.charityua_android

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfile>

    @GET("/fundraisers")
    suspend fun getFundraisers(): Response<List<Fundraiser>>

    @GET("/categories")
    suspend fun getCategories(): Response<List<Category>>
}