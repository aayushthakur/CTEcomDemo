package com.clevertap.demo.ecom.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.demo.ecom.carousel.CategoriesAdapter.OnItemClickListener
import com.clevertap.demo.ecom.databinding.CarouselImageLayoutBinding
import com.clevertap.demo.ecom.productExperiences.ImageModel
import com.squareup.picasso.Picasso

class CarouselBannerAdapter(private var imageModelList: ArrayList<ImageModel>) :
    RecyclerView.Adapter<CarouselBannerAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val binding =
            CarouselImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = imageModelList[position]
        holder.bind(model)
        holder.binding.root.setOnClickListener { view ->
            onItemClickListener!!.onClick(
                holder.binding.imageView,
                imageModelList[position].imageUrl,
                imageModelList[position].redirectUrl,
                imageModelList[position].order
            )
        }
    }


    inner class ViewHolder(val binding: CarouselImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ImageModel) {
            binding.apply {
                Picasso.get().load(model.imageUrl).into(binding.imageView)
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return imageModelList.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }


    interface OnItemClickListener {
        fun onClick(imageView: ImageView?, url: String?, redirectUrl: String?, order: Int?)
    }

    fun updateList(list: ArrayList<ImageModel>) {
        this.imageModelList = list
    }

    fun clearList() {
        this.imageModelList = ArrayList()
    }
}