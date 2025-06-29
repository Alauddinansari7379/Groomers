package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ehcf.Helper.currency
import com.example.groomers.adapter.Booking
import com.example.groomers.adapter.ImageSliderAdapter
import com.example.groomers.adapter.PopularServiceAdapter
import com.example.groomers.adapter.ViewPagerAdapter1
import com.example.groomers.databinding.ActivityBookingDetailBinding
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.ServiceViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class BookingDetail : AppCompatActivity(), Booking {

    private var vendorId: String? = null
    private var serviceId: String? = null
    private lateinit var binding: ActivityBookingDetailBinding
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: ServiceViewModel by viewModels()
    private lateinit var serviceAdapter: PopularServiceAdapter
    private val imageUrls = mutableListOf<String>()
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPriceSymbol.text= currency
        receiveData() // Receive intent data
        setupListeners()
//        setupRecyclerView()
        setupFragment()
        setupImageSlider()
        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
//                viewModel.getServiceList(token, sessionManager.userType.toString())
//                viewModel.getServiceList(token, "Female")
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
        vendorId = intent.getStringExtra("vendorId")
        serviceId = intent.getStringExtra("serviceId")
        val serviceDescription = intent.getStringExtra("service_description")
        val servicePrice = intent.getStringExtra("service_price")

        // Set data to UI elements
        binding.shopTitle.text = serviceName
        binding.shopAddress.text = address
        binding.ownerDetails.text = serviceDescription
        binding.tvPrice.text = servicePrice
//        serviceImage?.let {
//            val imageUrl = "https://groomers.co.in/public/uploads/$it"
//            Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.noimage)
//                .into(binding.headerImage)
//        }
        // Image list (duplicating the same image for slider)
        serviceImage?.let {
            val baseUrl = "https://groomers.co.in/public/uploads/"
            val imageUrl = baseUrl + it
            imageUrls.addAll(listOf(imageUrl, imageUrl, imageUrl))
            Log.e("ImageSlider",imageUrls.toString())
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

//    private fun setupRecyclerView() {
//        serviceAdapter = PopularServiceAdapter(emptyList(),this,this)
//        binding.rvPopularService.adapter = serviceAdapter
//    }

    override fun booking(
        serviceName: String,
        description: String,
        image: String,
        price: Int,
        user_type: String,
        id: String,
        userid: String,
        categoryId: String,
        address: String
    ) {
        val intent = Intent(this, ViewOrderDetails::class.java).apply {
            putExtra("serviceName", serviceName)
            putExtra("description", description)
            putExtra("image", image)
            putExtra("price", price)
            putExtra("vendorId", vendorId)
            putExtra("serviceId", serviceId)
            putExtra("user_type", user_type)
            putExtra("address", address)
        }
        startActivity(intent)
    }
    private fun setupFragment(){

        val adapter = ViewPagerAdapter1(this)
        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout1, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Service"
                1 -> "Reviews"
                2 -> "Portfolio"
                3 -> "Details"
                else -> "Tab"
            }
        }.attach()
    }
    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter(imageUrls)
        binding.imageSlider.adapter = imageSliderAdapter

        val runnable = object : Runnable {
            override fun run() {
                if (imageUrls.isNotEmpty()) {
                    currentPage = (currentPage + 1) % imageUrls.size
                    binding.imageSlider.setCurrentItem(currentPage, true)
                    sliderHandler.postDelayed(this, 3000)
                }
            }
        }
        sliderHandler.post(runnable)

        binding.imageSlider.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                sliderHandler.removeCallbacksAndMessages(null)
                sliderHandler.postDelayed(runnable, 3000)
            }
        })
    }
}
