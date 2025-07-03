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
    private var address: String = ""
    private var time: String = ""
    private var selectedDayNew: String = ""
    private var formattedDate: String = ""
    private var selectedDay: String = "1"

    private var selectedStartTime: String? = ""
    private var selectedEndTime: String? = ""
    private var id: String? = null
    private var selectedSeatCount: Int = 1

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
        address = intent?.getStringExtra("address") ?: ""
        time = intent?.getStringExtra("time") ?: ""

        binding.tvServiceName1.text = serviceName
        binding.tvServiceAddress.text = address

        setupRecyclerView()
        setupServiceDetails()
        observeViewModel()

        // ✅ Initialize current date
        val calendar = Calendar.getInstance()
        binding.calendarView.minDate = calendar.timeInMillis // Disable past dates

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1

        selectedDayNew = days[dayIndex]
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

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = formatter.format(calendar.time)

        fetchSlots()

        binding.btnContinueToPayment.setOnClickListener {
            if (selectedStartTime!!.isEmpty() || selectedEndTime!!.isEmpty()) {
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

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendarNew = Calendar.getInstance()
            calendarNew.set(year, month, dayOfMonth)

            val dayIndexNew = calendarNew.get(Calendar.DAY_OF_WEEK) - 1
            selectedDayNew = days[dayIndexNew]

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

            val formatterNew = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formattedDate = formatterNew.format(calendarNew.time)

            Log.d("SelectedDay", selectedDayNew)
            Log.d("SelectedDayNumber", selectedDay)
            Log.d("FormattedDate", formattedDate)

            fetchSlots()
        }
    }

    private fun setupRecyclerView() {
        timeSlotAdapter = TimeSlotAdapter(emptyList()) { result, seatCount ->
            selectedStartTime = result.start_time
            selectedEndTime = result.end_time
            selectedSeatCount = seatCount
            id = result.id.toString()
        }
        binding.rvTimeSlots.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTimeSlots.adapter = timeSlotAdapter
    }

    private fun setupServiceDetails() {
        binding.tvServiceName.text = serviceName
        binding.tvServiceDuration.text = time+" min"
        binding.tvServicePrice.text = "$$price"
        val imageUrl = "https://groomers.co.in/public/uploads/$image"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.noimage)
            .into(binding.ivServiceImage)
    }

    private fun fetchSlots() {
        selectedStartTime = ""
        viewModel.fetchSlotBooking(vendorId, categoryId, selectedDay, serviceId)
    }

    private fun observeViewModel() {
        viewModel.slotBookingData.observe(this) { response ->
            if (response != null && response.status == 1) {
                timeSlotAdapter.updateData(response.result)
            } else {
                Toast.makeText(this, "Failed to fetch time slots", Toast.LENGTH_SHORT).show()
            }
        }

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
                    textColor = null
                ).show()
            }
        }
    }
}
