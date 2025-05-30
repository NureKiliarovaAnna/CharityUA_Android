package com.example.charityua_android

import com.google.gson.annotations.SerializedName

data class FavoriteFundraiser(
    @SerializedName("fundraiser_id") val fundraiser_id: Int,
    val title: String,
    val description: String,
    @SerializedName("goal_amount") val goal_amount: Double,
    @SerializedName("current_amount") val current_amount: Double,
    val status: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("category_id") val category_id: Int,
    @SerializedName("category_name") val category_name: String,
    @SerializedName("organizer_name") val organizer_name: String,
    @SerializedName("cover_image_url") val cover_image_url: String?
)

