package com.example.groomers.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groomers.adapter.TimeSlotAdapter
import com.example.groomers.databinding.ActivityViewOrderDetailsBinding

class ViewOrderDetails : AppCompatActivity() {
    private lateinit var binding: ActivityViewOrderDetailsBinding
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val timeSlots = listOf(
        "10:00 AM" to "11:00 AM",
        "11:30 AM" to "12:30 PM",
        "2:00 PM" to "3:00 PM",
        "4:00 PM" to "5:00 PM",
        "6:00 PM" to "7:00 PM"
    )

    private val selectedService = Service("Haircut", "30 mins", 25.0)

    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupRecyclerView()
        setupServiceDetails()

//        binding.btnContinueToPayment.setOnClickListener {
//            if (selectedStartTime != null && selectedEndTime != null) {
//                val intent = Intent(this, PaymentActivity::class.java)
//                intent.putExtra("selectedStartTime", selectedStartTime)
//                intent.putExtra("selectedEndTime", selectedEndTime)
//                intent.putExtra("serviceName", selectedService.name)
//                intent.putExtra("servicePrice", selectedService.price)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun setupTabs() {
        days.forEach { day ->
            binding.tabLayoutDays.addTab(binding.tabLayoutDays.newTab().setText(day))
        }
    }

    private fun setupRecyclerView() {
        timeSlotAdapter = TimeSlotAdapter(timeSlots) { startTime, endTime ->
            selectedStartTime = startTime
            selectedEndTime = endTime
        }
        binding.rvTimeSlots.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTimeSlots.adapter = timeSlotAdapter
    }

    private fun setupServiceDetails() {
        binding.tvServiceName.text = selectedService.name
        binding.tvServiceDuration.text = "Duration: ${selectedService.duration}"
        binding.tvServicePrice.text = "Price: $${selectedService.price}"
    }
}
data class Service(val name: String, val duration: String, val price: Double)

