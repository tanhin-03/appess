package com.example.ess.adapters

import Item
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ess.R
//import com.example.ess.models.Item
import com.squareup.picasso.Picasso

class ProductAdapter(
    private val items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        val imageUrl = item.imageUrl
        if (imageUrl.isNullOrEmpty()) {
            // Sử dụng ảnh mặc định nếu URL trống
            Picasso.get()
                .load(R.drawable.painting) // Đảm bảo tên tệp đúng
                .into(holder.productImage)
        } else {
            // Tải ảnh từ URL
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(holder.productImage)
        }

        holder.addToCartButton.setOnClickListener { onItemClick(item) }
    }
    override fun getItemCount() = items.size
}