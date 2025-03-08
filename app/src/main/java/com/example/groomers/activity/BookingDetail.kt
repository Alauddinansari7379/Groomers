package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.adapter.Booking
import com.example.groomers.adapter.PopularServiceAdapter
import com.example.groomers.adapter.ServiceAdapter
import com.example.groomers.databinding.ActivityBookingDetailBinding
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.ServiceViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue
@AndroidEntryPoint
class BookingDetail : AppCompatActivity(), Booking {

    private lateinit var binding: ActivityBookingDetailBinding
    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: ServiceViewModel by viewModels()
    private lateinit var serviceAdapter: PopularServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiveData() // Receive intent data
        setupListeners()
        setupRecyclerView()
        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
//                viewModel.getServiceList(token, sessionManager.userType.toString())
                viewModel.getServiceList(token, "Female")
            }
        } ?: run {
            Toast.makeText(this, "Error: Missing Token", Toast.LENGTH_LONG).show()
        }
        viewModel.isLoading.observe(this@BookingDetail) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(this@BookingDetail)
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.modelService.observe(this) { response ->
            response?.result?.let { services ->
                serviceAdapter.updateData(services) // Update adapter data instead of reinitializing
            } ?: run {
                Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun receiveData() {
        val serviceName = intent.getStringExtra("service_name") ?: "No Name Provided"
        val serviceImage = intent.getStringExtra("service_image")
        val serviceType = intent.getStringExtra("service_type")
        val address = intent.getStringExtra("service_address")
        val serviceDescription = intent.getStringExtra("service_description")
        val servicePrice = intent.getStringExtra("service_price")

        // Set data to UI elements
        binding.shopTitle.text = serviceName
        binding.shopAddress.text = address
        binding.ownerDetails.text = serviceDescription
        binding.tvPrice.text = servicePrice
        serviceImage?.let {
            val imageUrl = "https://groomers.co.in/public/uploads/$it"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.noimage)
                .into(binding.headerImage)
        }
    }

    private fun setupListeners() {
//        binding.bookButton111.setOnClickListener {
//            startActivity(
//                Intent(this, Dashboard::class.java).apply {
//                    putExtra("navigate_to", "fragment_cart")
//                }
//            )
//            finish() // Close this activity after navigation
//        }

    }

    private fun setupRecyclerView() {
        serviceAdapter = PopularServiceAdapter(emptyList(),this,this)
        binding.rvPopularService.adapter = serviceAdapter
    }

    override fun booking() {
        startActivity(
                Intent(this, Dashboard::class.java).apply {
                    putExtra("navigate_to", "fragment_cart")
                }
            )
            finish()
    }
}
