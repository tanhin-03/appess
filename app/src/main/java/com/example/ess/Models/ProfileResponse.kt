package com.example.ess.Models

data class ProfileResponse(
    val accountId: Int,
    val fullName: String,
    val emailAddress: String,
    val avatar: String,
    val role: Int,
    val viewArtworks: List<Artwork>,
    val balance: Int
)
