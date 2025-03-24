package com.example.groomers.activity


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.adapter.TimeSlotAdapter
import com.example.groomers.databinding.ActivityViewOrderDetailsBinding
class ViewOrderDetails : AppCompatActivity() {
    private lateinit var binding: ActivityViewOrderDetailsBinding
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    private var serviceName: String = ""
    private var description: String = ""
    private var image: String = ""
    private var price: Int = 0
    private var userType: String = ""

    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val timeSlots = listOf(
        "10:00 AM" to "11:00 AM",
        "11:30 AM" to "12:30 PM",
        "2:00 PM" to "3:00 PM",
        "4:00 PM" to "5:00 PM",
        "6:00 PM" to "7:00 PM"
    )

    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Retrieve intent data inside onCreate()
        serviceName = intent?.getStringExtra("serviceName") ?: ""
        description = intent?.getStringExtra("description") ?: ""
        image = intent?.getStringExtra("image") ?: ""
        price = intent?.getIntExtra("price", 0) ?: 0
        userType = intent?.getStringExtra("user_type") ?: ""

        setupTabs()
        setupRecyclerView()
        setupServiceDetails()
    }

    private fun setupTabs() {
        days.forEach { day ->
            binding.tabLayoutDays.addTab(binding.tabLayoutDays.newTab().setText(day))
        }
    }

    private fun setupRecyclerView() {
        timeSlotAdapter = TimeSlotAdapter(timeSlots) { startTime, endTime, seatCount ->
            selectedStartTime = startTime
            selectedEndTime = endTime
            Log.d("TimeSlotSelection", "Selected Time: $startTime - $endTime, Seats: $seatCount")
        }
        binding.rvTimeSlots.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTimeSlots.adapter = timeSlotAdapter
    }


    private fun setupServiceDetails() {
        binding.tvServiceName.text = serviceName
        binding.tvServiceDuration.text = "Duration: ${30}"
        binding.tvServicePrice.text = "Price: $$price"
        val imageUrl = "https://groomers.co.in/public/uploads/$image"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.noimage)
            .into(binding.ivServiceImage)
    }
}
