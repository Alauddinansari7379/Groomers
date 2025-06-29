package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehcf.Helper.currency
import com.example.groomers.R
import com.example.groomers.databinding.BookingItemNewBinding
import com.example.groomers.model.modelbookinglist.Result

class BookingsAdapterCanclled(private val serviceList: List<Result>, private val context: Context) :
    RecyclerView.Adapter<BookingsAdapterCanclled.BookingsViewMode>() {

    // Filtered list with condition
    private val filteredList = serviceList.filter { it.slug == "rejected" }

    inner class BookingsViewMode(val binding: BookingItemNewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsViewMode {
        val binding = BookingItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingsViewMode(binding)
    }

    override fun getItemCount(): Int = filteredList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingsViewMode, position: Int) {
        with(filteredList[position]) {
            with(holder.binding) {
                if (filteredList[position].profile_picture != null) {
                    Glide.with(context)
                        .load("https://groomers.co.in/public/uploads/$profile_picture")
                        .into(holder.binding.imageView)
                } else {
                    imageView.setImageResource(R.drawable.errorimage) // Correct method to set the image
                }


                tvServiceName.text = serviceName
                tvDate.text = date
                tvPrice.text = currency + total.toString()
                tvCustomerName.text = vendorName
                tvGender.text = user_type
                tvStartTime.text = start_time
                tvEndTime.text = end_time
                tvAddress.text = address
                tvDescription.text = description
                tvBookingStatues.text = slug
                tvRating.text = rating.toString()
                when (slug) {
                    "waiting_for_accept" -> {
                        tvBookingStatues.background.setTint(ContextCompat.getColor(context, R.color.yellow))
                    }

                    "accepted" -> {
                        tvBookingStatues.background.setTint(ContextCompat.getColor(context, R.color.green))

                    }

                    "rejected" -> {
                        tvBookingStatues.background.setTint(ContextCompat.getColor(context, R.color.red))
                     }

                    "completed" -> {
                        tvBookingStatues.background.setTint(ContextCompat.getColor(context, R.color.green))
                     }
                }

            }

        }
    }
}

