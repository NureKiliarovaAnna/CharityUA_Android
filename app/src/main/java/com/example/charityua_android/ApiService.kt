package com.example.charityua_android

import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface ApiService {

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    @POST("/api/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Void>

    @POST("/api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>

    @GET("/api/public/fundraisers/active")
    suspend fun getActiveFundraisers(): Response<List<Fundraiser>>

    @GET("/api/public/fundraisers/{id}")
    suspend fun getFundraiserById(@Path("id") id: Int): Response<Fundraiser>

    @POST("/api/donate")
    suspend fun postDonation(@Body request: DonationRequest): Response<Unit>

    @POST("/api/complaints")
    suspend fun submitComplaint(@Body request: ComplaintRequest): Response<Void>

    @GET("/api/me")
    suspend fun getProfile(): Response<UserProfile>

    @GET("/api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("/api/donations/my")
    suspend fun getMyDonations(): Response<List<DonationWithFundraiser>>

    @GET("/api/favorites")
    suspend fun getFavorites(): Response<List<FavoriteFundraiser>>

    @POST("/api/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<Fundraiser>

    @DELETE("/api/favorites/{fundraiserId}")
    suspend fun removeFavorite(@Path("fundraiserId") fundraiserId: Int): Response<Void>

    @Multipart
    @PUT("/api/profile/mobile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part parts: List<MultipartBody.Part>
    ): Response<ResponseBody>

    @POST("/api/liqpay/checkout")
    suspend fun getLiqPayData(@Body request: LiqPayRequest): Response<LiqPayResponse>

    @POST("/api/donation/success")
    suspend fun postDonationSuccess(@Body request: DonationRequest): Response<Void>

    @POST("/api/chats")
    suspend fun createChat(@Body request: CreateChatRequest): Response<CreateChatResponse>

    @GET("/api/chats/{chatId}/messages")
    suspend fun getChatMessages(@Path("chatId") chatId: Int): Response<List<ChatMessageResponse>>

    @POST("/api/chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: Int,
        @Body request: SendMessageRequest
    ): Response<Void>

    @GET("/api/chats")
    suspend fun getMyChats(): Response<List<ChatSummary>>

    @POST("/api/users/fcm")
    suspend fun updateFcmToken(@Body token: FcmTokenRequest): Response<Void>

    @POST("/api/chats/{chatId}/read")
    suspend fun markMessagesAsRead(@Path("chatId") chatId: Int): Response<Unit>
}