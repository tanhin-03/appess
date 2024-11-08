package com.example.ess.utils;

import com.example.ess.DTO.ResponseData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("post/")
    Call<ResponseData> getPosts();
}