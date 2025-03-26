package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.databinding.BookingsRowBinding
import com.example.groomers.model.modelbookinglist.Result


class AdapterConfirmeBooking(serviceList: List<Result>) : RecyclerView.Adapter<AdapterConfirmeBooking.BookingsViewMode1>() {

    private val filteredList = serviceList.filter { it.slug == "Accepted" }

    inner class BookingsViewMode1(val binding: BookingsRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsViewMode1 {
        val binding = BookingsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingsViewMode1(binding)
    }

    override fun getItemCount(): Int = filteredList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingsViewMode1, position: Int) {
        with(filteredList[position]) {
            holder.binding.tvId.text = id.toString()
            holder.binding.tvDescription.text = description
            holder.binding.tvServiceType.text = serviceType
            holder.binding.tvUserType.text = user_type
            holder.binding.tvTime.text = time
            holder.binding.tvPrice.text = price.toString()
        }
    }
}
