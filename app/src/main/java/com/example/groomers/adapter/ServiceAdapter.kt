package com.example.groomers.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehcf.Helper.currency
import com.example.groomers.R
import com.example.groomers.databinding.ServiceRowBinding
import com.example.groomers.model.modelservice.Result

class ServiceAdapter(
    private var serviceList: List<Result>,
    private val onItemClick: (Result) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(val binding: ServiceRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: Result) {
            with(binding) {
                val imageUrl = "https://groomers.co.in/public/uploads/${service.image}"

                Glide.with(root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.photo)
                    .into(ivImage)

                root.setOnClickListener {
                    Log.d("ServiceAdapter", "Item Clicked: ${service.serviceName}") // Debug log
                    onItemClick(service) // Call click listener
                }
                tvPrice.text = "$currency"+service.price.toString()
                tvDescription.text = service.description
                tvServiceName.text = service.serviceName
                tvAddress.text = service.address
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ServiceRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(serviceList[position])
    }

    override fun getItemCount(): Int = serviceList.size

    fun updateData(newList: List<Result>) {
        serviceList = newList
        notifyDataSetChanged()
    }
}
