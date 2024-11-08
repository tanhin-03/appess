package com.example.ess.utils;

import com.example.ess.DTO.ResponseData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("post")
    fun getPosts(): Call<ResponseData>
}