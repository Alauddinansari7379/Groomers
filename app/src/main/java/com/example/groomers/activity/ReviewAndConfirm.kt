package com.example.groomers.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityReviewAndConfirmBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.BookingViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ReviewAndConfirm : AppCompatActivity() {
    private val binding by lazy { ActivityReviewAndConfirmBinding.inflate(layoutInflater) }
    private val viewModel: BookingViewModel by viewModels()
    private var changeTextColor: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val selectedStartTime = intent.getStringExtra("selectedStartTime")
        val selectedEndTime = intent.getStringExtra("selectedEndTime")
        val serviceName = intent.getStringExtra("serviceName")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("description")
        val selectedSeats = intent.getStringExtra("selectedSeats")
        val slotId = intent.getStringExtra("slotId")
        binding.tvPrice.text = price
        binding.tvPrice1.text = price
        binding.tvDescription.text = description
        binding.tvServiceName.text = serviceName
        val date = getCurrentDate()
        observeViewModel()
        binding.tvSlotTime.text = "$selectedStartTime-$selectedEndTime"
        binding.btnContinue.setOnClickListener {
            sessionManager.accessToken?.let { token ->
                viewModel.createBooking(
                    token,
                    customerId = 11111,
                    vendorId = 10,
                    price!!.toInt(),
                    paymentMode = 332,
                    slotId!!.toInt(),
                    serviceId = 21,
                    date,
                    time = "10:00",
                    notes = ""
                )
            } ?: showError("Error: Missing Token")
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
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
                if (response.status == 1) {
                    Toastic.toastic(
                        context = this@ReviewAndConfirm,
                        message = "Booking successful!",
                        duration = Toastic.LENGTH_SHORT,
                        type = Toastic.SUCCESS,
                        isIconAnimated = true,
                        textColor = if (changeTextColor) Color.BLUE else null,
                    ).show()

                    // Clear all previous activities and navigate to the desired activity
                    val intent = Intent(this@ReviewAndConfirm, BookingDetail::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish() // Optional, just to be safe
                }else{
                    Toastic.toastic(
                        context = this@ReviewAndConfirm,
                        message = response.message,
                        duration = Toastic.LENGTH_SHORT,
                        type = Toastic.ERROR,
                        isIconAnimated = true,
                        textColor = if (changeTextColor) Color.BLUE else null,
                    ).show()
                }
            }
        }

    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
