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

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    @POST("/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Void>

    @POST("/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>

    @GET("/moderator/fundraisers/active")
    suspend fun getFundraisers(): Response<List<Fundraiser>>

    @GET("/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfile>

    @GET("/categories")
    suspend fun getCategories(): Response<List<Category>>
}