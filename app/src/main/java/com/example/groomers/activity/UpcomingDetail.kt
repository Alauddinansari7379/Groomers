package com.example.groomers.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ehcf.Helper.currency
import com.example.groomers.R
import com.example.groomers.databinding.ActivityUpcomingDetailBinding
import com.example.groomers.model.modelbookinglist.Result
import com.google.gson.Gson

class UpcomingDetail : AppCompatActivity() {
    private lateinit var binding: ActivityUpcomingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data
        val json = intent.getStringExtra("data")
        val item = Gson().fromJson(json, Result::class.java)

        // Populate UI elements
        binding.tvServiceName.text = item.serviceName
        binding.tvCustomerName.text = item.vendorName
        binding.tvDate.text = item.date
        binding.tvStartTime.text = item.start_time
        binding.tvEndTime.text = item.end_time
        binding.tvAddress.text = item.currentAddress
        binding.tvStatus.text = item.status_for_customer
        binding.tvDescription.text = item.description
        binding.tvPrice.text = currency + item.total.toString()
        binding.tvRating.text = item.rating.toString()
        binding.tvReview.text = item.comments.toString()
        binding.tvUserType.text = item.user_type

        // Load image using Glide
        Glide.with(this)
            .load("https://groomers.co.in/public/uploads/${item.profile_picture}")
            .placeholder(R.drawable.errorimage)
            .into(binding.ivProfileImage)
    }
}
