package com.example.ess

//import com.example.ess.models.Item
//import com.example.ess.models.ItemResponse
import Item
import ItemResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.utils.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private var items: List<Item> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRetrofit()
        setupViews()
        setupRecyclerView()
        loadItems()

    }

    private fun setupRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl("http://poserdungeon.myddns.me:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun setupViews() {
        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }
        setupBottomNavigationView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.productsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Thêm dữ liệu test
        val testItems = listOf(
            Item(1, "Test Item 1", "https://example.com/image1.jpg"),
            Item(2, "Test Item 2", "https://example.com/image2.jpg"),
            Item(3, "Test Item 3", "https://example.com/image3.jpg")
        )
        recyclerView.adapter = ProductAdapter(testItems) { item ->
            navigateToCartActivity(
                productName = item.itemName,
                productPrice = "VND ${(item.itemId * 1000)}.00",
                productImageResId = R.drawable.placeholder,
                productDescription = "Description for ${item.itemName}"
            )
        }
    }

    private fun loadItems() {
        Log.d("ProductActivity", "Loading items...")
        apiService.getItems().enqueue(object : Callback<ItemResponse> {
            override fun onResponse(call: Call<ItemResponse>, response: Response<ItemResponse>) {
                Log.d("ProductActivity", "Response received: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let { itemResponse ->
                        Log.d("ProductActivity", "Items loaded: ${itemResponse.items.size}")
                        items = itemResponse.items
                        // Kiểm tra xem items có dữ liệu không
                        if (items.isNotEmpty()) {
                            recyclerView.adapter = ProductAdapter(items) { item ->
                                navigateToCartActivity(
                                    productName = item.itemName,
                                    productPrice = "VND ${(item.itemId * 1000)}.00",
                                    productImageResId = R.drawable.placeholder,
                                    productDescription = "Description for ${item.itemName}"
                                )
                            }
                            // Thông báo adapter cập nhật dữ liệu
                            recyclerView.adapter?.notifyDataSetChanged()
                        } else {
                            Log.e("ProductActivity", "Items list is empty")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductActivity", "Error response: $errorBody")
                    Toast.makeText(this@ProductActivity,
                        "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ItemResponse>, t: Throwable) {
                Log.e("ProductActivity", "API call failed", t)
                Toast.makeText(this@ProductActivity,
                    "Error loading items: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun navigateToCartActivity(productName: String, productPrice: String, productImageResId: Int, productDescription: String) {
        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra("productName", productName)
        intent.putExtra("productPrice", productPrice)
        intent.putExtra("productImageResId", productImageResId)
        intent.putExtra("productDescription", productDescription)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_products
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_products -> {
                    true
                }
                R.id.navigation_promotions -> {
                    val intent = Intent(this, PromotionsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    inner class ProductAdapter(
        private val items: List<Item>,
        private val onItemClick: (Item) -> Unit
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val productImage: ImageView = view.findViewById(R.id.productImage)
            val productName: TextView = view.findViewById(R.id.productName)
            val productPrice: TextView = view.findViewById(R.id.productPrice)
            val addToCartButton: Button = view.findViewById(R.id.addToCartButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val item = items[position]
            holder.productName.text = item.itemName
            holder.productPrice.text = "VND ${item.itemId * 1000}.00"
            Picasso.get().load(item.imageUrl).into(holder.productImage)
            holder.addToCartButton.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount() = items.size
    }
}