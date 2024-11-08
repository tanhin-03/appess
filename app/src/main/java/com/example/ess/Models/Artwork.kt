package com.example.ess.Models

data class Artwork(
    val artworkId: Int,
    val name: String,
    val description: String,
    val image: String,
    val price: Int,
    val artistID: Int,
    val isPublic: Boolean,
    val isBuyAvailable: Boolean,
    val artworkRating: Double,
    val artworkDate: String,
    val genreId: Int,
    val genreName: String?,
    val membersRated: List<String>
)
