package com.example.groomers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.groomers.databinding.PopulerServiceRowBinding
import com.example.groomers.model.modelservice.Result

class PopularServiceAdapter(private var categoryList : List<Result>, val context : Context,val booking : Booking):RecyclerView.Adapter<PopularServiceAdapter.PopularViewHolder>(){
    inner class PopularViewHolder(val binding : PopulerServiceRowBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopulerServiceRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PopularViewHolder(binding);
    }
    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        with(categoryList[position]) {
            holder.binding.tvServiceName.text = serviceName
            holder.binding.tvDescription.text = description
            Glide.with(context)
                .load("https://groomers.co.in/public/uploads/" + image)
                .into(holder.binding.ivServiceImage)
            holder.binding.btnBook.setOnClickListener {
                booking.booking(serviceName,description,image,price,user_type)
            }
        }

    }
    fun updateData(newList: List<Result>) {
        categoryList = newList
        notifyDataSetChanged()
    }
}
interface Booking{
    fun booking(
        serviceName: String,
        description: String,
        image: String,
        price: Int,
        user_type: String,
    )
}