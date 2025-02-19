package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.databinding.ActivityBookingDetailBinding
class BookingDetail : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiveData() // Receive intent data
        setupListeners()
    }

    private fun receiveData() {
        val serviceName = intent.getStringExtra("service_name") ?: "No Name Provided"
        val serviceImage = intent.getStringExtra("service_image")
        val serviceType = intent.getStringExtra("service_type")
        val address = intent.getStringExtra("service_address")
        val serviceDescription = intent.getStringExtra("service_description")

        // Set data to UI elements
        binding.shopTitle.text = serviceName  // Assuming there's a TextView to show the service name
        binding.shopAddress.text = address  // Assuming there's a TextView to show the service name
        binding.ownerDetails.text = serviceDescription  // Assuming there's a TextView to show the service name

        // Load image with Glide if serviceImage is not null
        serviceImage?.let {
            val imageUrl = "https://groomers.co.in/public/uploads/$it"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.noimage)
                .into(binding.headerImage)  // Assuming there's an ImageView to show the service image
        }
    }

    private fun setupListeners() {
        binding.bookButton111.setOnClickListener {
            startActivity(
                Intent(this, Dashboard::class.java).apply {
                    putExtra("navigate_to", "fragment_cart")
                }
            )
            finish() // Close this activity after navigation
        }
    }
}
