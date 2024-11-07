package com.example.ess.utils

//import com.example.ess.models.Item
//import com.example.ess.models.ItemResponse
import ItemResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("items")
    fun getItems(): Call<ItemResponse>  // Thay vì Call<List<Item>>
}