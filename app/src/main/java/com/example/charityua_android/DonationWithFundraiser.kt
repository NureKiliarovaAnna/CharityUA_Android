package com.example.charityua_android

data class DonationWithFundraiser(
    val donation_id: Int,
    val amount: Int,
    val created_at: String,
    val fundraiser: Fundraiser  // уже існує клас
)
