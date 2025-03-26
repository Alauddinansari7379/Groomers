package com.example.groomers.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityReviewAndConfirmBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.BookingViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReviewAndConfirm : AppCompatActivity() {
    private val binding by lazy { ActivityReviewAndConfirmBinding.inflate(layoutInflater) }
    private val viewModel: BookingViewModel by viewModels()
    private var changeTextColor: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val selectedStartTime = intent.getStringExtra("selectedStartTime")
        val selectedEndTime = intent.getStringExtra("selectedEndTime")
        val serviceName = intent.getStringExtra("serviceName")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("description")
        val selectedSeats = intent.getIntExtra("selectedSeats", 1)
        binding.tvPrice.text = price
        binding.tvPrice1.text = price
        binding.tvDescription.text = description
        binding.tvServiceName.text = serviceName
        observeViewModel()
        binding.tvSlotTime.text = "$selectedStartTime-$selectedEndTime"
        binding.btnContinue.setOnClickListener {
            sessionManager.accessToken?.let { token ->
                viewModel.createBooking(
                    token,
                    customerId = 11111,
                    vendorId = 10,
                    total = 5009999,
                    paymentMode = 332,
                    slotId = 34,
                    serviceId = 21,
                    date = "2025-02-17",
                    time = "10:00",
                    notes = ""
                )
            } ?: showError("Error: Missing Token")
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(this)
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showError(errorMessage)
            }
        }

        viewModel.bookingResult.observe(this) { response ->
            response?.let {

                Toastic.toastic(
                    context = this@ReviewAndConfirm,
                    message = "Booking successful!",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (changeTextColor) Color.BLUE else null,
                ).show()
                // Handle successful booking (e.g., navigate to another activity)
                finish()
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
