package com.clevertap.demo.ecom.mainFragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clevertap.demo.ecom.R

class CartAdapter(
    private var products: MutableList<Product>,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.title
        holder.price.text = "₹${product.price}"
        Glide.with(holder.image.context).load(product.image_url).into(holder.image)

        holder.removeButton.setOnClickListener {
            onRemoveClick(product)
        }
    }

    override fun getItemCount() = products.size

    fun updateData(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.cart_item_image)
        val title: TextView = itemView.findViewById(R.id.cart_item_title)
        val price: TextView = itemView.findViewById(R.id.cart_item_price)
        val removeButton: ImageButton = itemView.findViewById(R.id.remove_cart_item)
    }
}