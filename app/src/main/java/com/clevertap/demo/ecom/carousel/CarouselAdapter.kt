package com.clevertap.demo.ecom.carousel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.demo.ecom.databinding.ImageLayoutBinding
import com.squareup.picasso.Picasso


class CarouselAdapter(private var context: Context, private var mList: List<ImageModel>) :
    RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val binding = ImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position]
        holder.bind(model)
        holder.binding.root.setOnClickListener { view ->
            onItemClickListener!!.onClick(
                holder.binding.imageView, mList[position].imageUrl, mList[position].redirectUrl
            )
        }/* with(holder) {
            with(mList[position]) {
                Glide
                    .with(context)
                    .load(this.imageUrl)
                    .centerCrop()
                    .into(binding.carouselImage);

//                binding.carouselImage.tooltipText = this.imageUrl
                binding.root.setOnClickListener { view ->
                    onItemClickListener!!.onClick(
                        binding.carouselImage, mList.get(position).imageUrl
                    )
                }
            }
        }*/
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ImageModel) {
            binding.apply {
                Picasso.get().load(model.imageUrl).centerCrop().into(binding.imageView)
                imageView.tooltipText = model.text
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }


    interface OnItemClickListener {
        fun onClick(imageView: ImageView?, url: String?, redirectUrl: String?)
    }

    fun updateList(list: List<ImageModel>) {
        this.mList = list
    }

}