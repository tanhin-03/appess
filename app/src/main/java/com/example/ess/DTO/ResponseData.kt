package com.example.ess.DTO

data class ResponseData(
        var totalItems: Int,
        var totalPages: Int,
        var pageSize: Int,
        var page: Int,
        var items: List<Post>
)