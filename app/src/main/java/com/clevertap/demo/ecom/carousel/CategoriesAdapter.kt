package com.clevertap.demo.ecom.carousel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.demo.ecom.databinding.ImageLayoutBinding
import com.clevertap.demo.ecom.productExperiences.ImageModel
import com.squareup.picasso.Picasso


class CategoriesAdapter(private var context: Context, private var mList: List<ImageModel>) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position]
        holder.bind(model)
        holder.binding.root.setOnClickListener { 
            model.text?.let { it1 -> onItemClickListener?.onClick(it1) }
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ImageModel) {
            binding.apply {
                Picasso.get().load(model.imageUrl).into(binding.imageView)
                itemText.text = model.text
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }


    interface OnItemClickListener {
        fun onClick(categoryName: String)
    }

    fun updateList(list: List<ImageModel>) {
        this.mList = list
    }

    fun clearList() {
        this.mList = ArrayList()
    }

}