package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groomers.databinding.BookingsRowBinding
import com.example.groomers.model.modelbookinglist.Result
class BookingsAdapter(private val serviceList: List<Result>, private val context: Context) :
    RecyclerView.Adapter<BookingsAdapter.BookingsViewMode>() {

    // Filtered list with condition
    private val filteredList = serviceList.filter { it.slug == "waiting_for_accept" }

    inner class BookingsViewMode(val binding: BookingsRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsViewMode {
        val binding = BookingsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingsViewMode(binding)
    }

    override fun getItemCount(): Int = filteredList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingsViewMode, position: Int) {
        with(filteredList[position]) {
            holder.binding.tvId.text = id.toString()
            holder.binding.tvDescription.text = description
            holder.binding.tvServiceType.text = serviceType
            holder.binding.tvUserType.text = user_type
            holder.binding.tvTime.text = time
            holder.binding.tvPrice.text = price.toString()

            // Using passed context directly
            Glide.with(context)
                .load("https://groomers.co.in/public/uploads/$profile_picture")
                .into(holder.binding.ivServiceImage)
        }
    }
}

