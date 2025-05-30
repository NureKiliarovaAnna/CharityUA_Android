package com.example.charityua_android

import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface ApiService {

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    @POST("/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Void>

    @POST("/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>

    @GET("/public/fundraisers/active")
    suspend fun getActiveFundraisers(): Response<List<Fundraiser>>

    @GET("public/fundraisers/{id}")
    suspend fun getFundraiserById(@Path("id") id: Int): Response<Fundraiser>

    @POST("/donate")
    suspend fun postDonation(@Body request: DonationRequest): Response<Unit>

    @POST("/complaints")
    suspend fun submitComplaint(@Body request: ComplaintRequest): Response<Void>

    @GET("/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfile>

    @GET("/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("/donations/my")
    suspend fun getMyDonations(): Response<List<DonationWithFundraiser>>

    @GET("/favorites")
    suspend fun getFavorites(): Response<List<Fundraiser>>

    @POST("/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<Fundraiser>

    @DELETE("/favorites/{fundraiserId}")
    suspend fun removeFavorite(@Path("fundraiserId") fundraiserId: Int): Response<Void>

    @Multipart
    @PUT("profile/mobile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part parts: List<MultipartBody.Part>
    ): Response<ResponseBody>
}