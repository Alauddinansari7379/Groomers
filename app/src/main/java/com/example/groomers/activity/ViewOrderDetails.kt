package com.example.groomers.activity


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.adapter.TimeSlotAdapter
import com.example.groomers.databinding.ActivityViewOrderDetailsBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.viewModel.SlotBookingViewModel
import com.google.android.material.tabs.TabLayout
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ViewOrderDetails : AppCompatActivity() {
    private lateinit var vendorId: String
    private lateinit var serviceId: String
    private lateinit var binding: ActivityViewOrderDetailsBinding
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private val viewModel: SlotBookingViewModel by viewModels()

    private var serviceName: String = ""
    private var description: String = ""
    private var image: String = ""
    private var price: Int = 0
    private var userType: String = ""
    private var categoryId: String = ""
    private var selectedDayNew: String = ""
    private var formattedDate: String = ""
    private var selectedDay: String = "1"  // Default to Monday

    private var selectedStartTime: String? = ""
    private var selectedEndTime: String? = ""
    private var id: String? = null
    private var selectedSeatCount: Int = 1 // Default 1 seat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Retrieve intent data
        serviceName = intent?.getStringExtra("serviceName") ?: ""
        description = intent?.getStringExtra("description") ?: ""
        vendorId = intent?.getStringExtra("vendorId") ?: ""
        serviceId = intent?.getStringExtra("serviceId") ?: ""
        image = intent?.getStringExtra("image") ?: ""
        price = intent?.getIntExtra("price", 0) ?: 0
        userType = intent?.getStringExtra("user_type") ?: ""
        categoryId = intent?.getStringExtra("categoryId") ?: ""

       // setupTabs()
        setupRecyclerView()
        setupServiceDetails()
        observeViewModel()

        // Fetch slots for default day
        fetchSlots()

        binding.btnContinueToPayment.setOnClickListener {
            if (selectedStartTime!!.isEmpty()|| selectedEndTime!!.isEmpty()) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this@ViewOrderDetails, ReviewAndConfirm::class.java).apply {
                putExtra("selectedStartTime", selectedStartTime)
                putExtra("formattedDate", formattedDate)
                putExtra("selectedEndTime", selectedEndTime)
                putExtra("selectedSeats", selectedSeatCount.toString())
                putExtra("serviceName", serviceName)
                putExtra("price", price.toString())
                putExtra("description", description)
                putExtra("slotId", id)
                putExtra("serviceId", serviceId)
                putExtra("vendorId", vendorId)
            }
            startActivity(intent)
        }
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")


        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            val dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 1, so subtract 1
             selectedDayNew = days[dayIndex] // Get "Mon", "Tue", etc.

            Log.d("SelectedDay", selectedDayNew)
            selectedDay = when (selectedDayNew) {
                "Mon" -> "1"
                "Tue" -> "2"
                "Wed" -> "3"
                "Thu" -> "4"
                "Fri" -> "5"
                "Sat" -> "6"
                "Sun" -> "7"
                else -> "1"
            }
            fetchSlots()
            Log.d("SelectedDayNumber", selectedDay ?: "null")
            // Set only date (time stays as current system time)
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Format: Fri, Jan 3, 2025 • 10:41 AM (or whatever current time is)
           // val formatter = SimpleDateFormat("EEE, MMM d, yyyy • hh:mm a", Locale.getDefault())
            val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

            val formattedDateNew = formatter.format(calendar.time)
            formattedDate = formattedDateNew

            Log.d("FormattedDate", formattedDate)
        }



    }

/*    private fun setupTabs() {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        days.forEachIndexed { index, day ->
            binding.tabLayoutDays.addTab(binding.tabLayoutDays.newTab().setText(day))
        }

        binding.tabLayoutDays.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedDay =
                    (tab?.position?.plus(1)).toString() // Convert to "1" for Monday, "2" for Tuesday, etc.
                fetchSlots()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }*/

    private fun setupRecyclerView() {
        timeSlotAdapter =
            TimeSlotAdapter(emptyList()) { result, seatCount -> // ✅ Receive full result object
                selectedStartTime = result.start_time
                selectedEndTime = result.end_time
                selectedSeatCount = seatCount
                id = result.id.toString()
            }
        binding.rvTimeSlots.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTimeSlots.adapter = timeSlotAdapter
    }

    private fun setupServiceDetails() {
        binding.tvServiceName.text = serviceName
        binding.tvServiceDuration.text = "Duration: 30 mins"
        binding.tvServicePrice.text = "Price: $$price"
        val imageUrl = "https://groomers.co.in/public/uploads/$image"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.noimage)
            .into(binding.ivServiceImage)
    }

    private fun fetchSlots() {
        selectedStartTime=""
        val vendorId = vendorId  // Set dynamically if required
        val categoryId = categoryId    // Set dynamically if required
        val serviceId = serviceId  // Set dynamically if required

        viewModel.fetchSlotBooking(vendorId, categoryId, selectedDay, serviceId)
    }

    private fun observeViewModel() {
        viewModel.slotBookingData.observe(this, Observer { response ->
            if (response != null && response.status == 1) {
                timeSlotAdapter.updateData(response.result)
            } else {
                Toast.makeText(this, "Failed to fetch time slots", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(this)
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toastic.toastic(
                    context = this@ViewOrderDetails,
                    message = errorMessage,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }
    }
}
