package com.example.groomers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehcf.Helper.currency
import com.example.groomers.R
import com.example.groomers.activity.UpcomingDetail
import com.example.groomers.databinding.BookingItemNewBinding
import com.example.groomers.model.modelbookinglist.Result
import com.google.gson.Gson

class BookingsAdapter(
    private val serviceList: List<Result>,
    private val context: Context,
    val review: Review
) :
    RecyclerView.Adapter<BookingsAdapter.BookingsViewMode>() {

    // Filtered list with condition
    private val filteredList = serviceList.filter { it.slug == "waiting_for_accept" }

    inner class BookingsViewMode(val binding: BookingItemNewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsViewMode {
        val binding =
            BookingItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                tvDescription.text = description
                tvBookingStatues.text = status_for_customer
                tvRating.text = rating.toString()
                when (slug) {
                    "waiting_for_accept" -> {
                        tvBookingStatues.background.setTint(
                            ContextCompat.getColor(
                                context,
                                R.color.yellow
                            )
                        )
                    }

                    "accepted" -> {
                        tvBookingStatues.background.setTint(
                            ContextCompat.getColor(
                                context,
                                R.color.green
                            )
                        )

                    }

                    "rejected" -> {
                        tvBookingStatues.background.setTint(
                            ContextCompat.getColor(
                                context,
                                R.color.red
                            )
                        )
                    }

                    "completed" -> {
                        tvBookingStatues.background.setTint(
                            ContextCompat.getColor(
                                context,
                                R.color.green
                            )
                        )
                        btnReview.visibility = View.GONE
                    }
                }
                root.setOnClickListener {
                    val intent = Intent(context, UpcomingDetail::class.java).apply {
                        putExtra("data", Gson().toJson(filteredList[position])) // Send as JSON
                    }
                    context.startActivity(intent)
                }
                btnReview.setOnClickListener {
                    review.rating(id.toString())
                }

            }

        }
    }

    interface Review {
        fun rating(bookingId: String)

    }
}

