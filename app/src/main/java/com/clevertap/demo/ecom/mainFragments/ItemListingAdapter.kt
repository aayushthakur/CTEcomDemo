package com.clevertap.demo.ecom.mainFragments

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clevertap.demo.ecom.R

class ItemListingAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ItemListingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listing_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.title
        holder.price.text = "₹${product.price}"
        holder.originalPrice.text = "₹${product.original_price}"
        holder.originalPrice.paintFlags = holder.originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.discount.text = product.discount
        Glide.with(holder.image.context).load(product.image_url).into(holder.image)
    }

    override fun getItemCount() = products.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val price: TextView = itemView.findViewById(R.id.item_price)
        val originalPrice: TextView = itemView.findViewById(R.id.item_original_price)
        val discount: TextView = itemView.findViewById(R.id.item_discount)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favorite_button)
    }
}