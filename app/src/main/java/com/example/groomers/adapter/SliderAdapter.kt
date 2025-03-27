package com.example.groomers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groomers.R

class SliderAdapter(
    private var imageList: List<String>
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageSlider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load("https://groomers.co.in/public/uploads/"+imageList[position]) // Load image using Glide
            .placeholder(R.drawable.man) // Default placeholder if the image fails to load
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    // Add a method to update data dynamically
    fun updateData(newImageList: List<String>) {
        imageList = newImageList
        notifyDataSetChanged()
    }
}
