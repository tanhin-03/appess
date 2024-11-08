package com.example.ess.DTO

data class Post(
        var postId: Int,
        var title: String,
        var description: String,
        var memberId: Int,
        var artworkId: Int,
        var comments: List<String> = emptyList()
)