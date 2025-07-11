package com.example.groomers.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ehcf.Helper.convertTo12Hour
import com.example.ehcf.Helper.currency
import com.example.groomers.adapter.TimeSlotAdapter.Companion.seat
import com.example.groomers.databinding.ActivityReviewAndConfirmBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.BookingViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
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
        val formattedDate = intent.getStringExtra("formattedDate")
        val serviceId = intent.getStringExtra("serviceId")
        val vendorId = intent.getStringExtra("vendorId")
        val time = intent.getStringExtra("time")
        val overAllRating = intent.getStringExtra("overAllRating")
        var selectedDayNew = intent.getStringExtra("formattedDate")
        binding.tvPrice1.text = currency + price
        binding.tvDescription.text = description
        binding.tvServiceName.text = serviceName
        binding.tvDuration.text = time
        binding.tvRating.text = if (overAllRating.isNullOrBlank() || overAllRating == "null") {
            "0"
        } else {
            overAllRating
        }


        val date = getCurrentDate()

        if (selectedDayNew!!.isEmpty()) {
            val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val currentDate = formatter.format(Date())
            selectedDayNew = currentDate
        }
        seat
        binding.tvCurrentDateTime.text = selectedDayNew + " " + convertTo12Hour(selectedStartTime!!)
        binding.tvSlotTime.text =
            convertTo12Hour(selectedStartTime!!) + " To " + convertTo12Hour(selectedEndTime!!)

        observeViewModel()

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnContinue.setOnClickListener {
            if (sessionManager.address.isNullOrEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Please add your address.")
                    .setConfirmText("Add address")
                    .showCancelButton(true)
                    .setConfirmClickListener { sDialog ->
                        sDialog.cancel()
                        startActivity(Intent(this, AddressList::class.java))
                    }
                    .setCancelClickListener { sDialog ->
                        sDialog.cancel()
                    }
                    .show()
            } else {
                sessionManager.accessToken?.let { token ->
                    if (formattedDate != null) {
                        viewModel.createBooking(
                            token,
                            sessionManager.userId!!.toInt(),
                            vendorId!!.toInt(),
                            price!!.toInt(),
                            paymentMode = 2,
                            slotId!!.toInt(),
                            serviceId!!.toInt(),
                            formattedDate,
                            getCurrentTime(),
                            notes = "",
                            selectedSeats!!,
                            sessionManager.address.toString()
                        )
                    }
                } ?: showError("Error: Missing Token")
            }
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
                    val intent = Intent(this@ReviewAndConfirm, Dashboard::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish() // Optional, just to be safe
                } else {
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

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getFormattedCurrentDateTime(): String {
        val date = Date()
        val formatter = SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.ENGLISH)
        return formatter.format(date)
    }
}
