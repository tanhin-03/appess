package com.example.ess

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.DTO.Post
import com.example.ess.DTO.ResponseData
import com.example.ess.utils.PostAdapter
import com.example.ess.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post) // Đảm bảo layout này có RecyclerView

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPosts()
    }

    private fun fetchPosts() {
        RetrofitClient.instance.getPosts().enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseData ->
                        displayPosts(responseData.items)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun displayPosts(posts: List<Post>) {
        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter
    }
}