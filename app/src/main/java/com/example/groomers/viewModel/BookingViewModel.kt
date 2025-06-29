package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelbooking.ModelBooking
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _bookingResult = MutableLiveData<ModelBooking?>()
    val bookingResult: LiveData<ModelBooking?> = _bookingResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun createBooking(
        token: String,
        customerId: Int,
        vendorId: Int,
        total: Int,
        paymentMode: Int,
        slotId: Int,
        serviceId: Int,
        date: String,
        time: String,
        notes: String? = null,
        sit: String,
        currentAddress: String,
    ) {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val response = apiService.createBooking(
                    "Bearer $token",
                    customerId,
                    vendorId,
                    total,
                    paymentMode,
                    slotId,
                    serviceId,
                    date,
                    time,
                    notes,
                    sit,
                    currentAddress
                )

                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        if (result.status == 1) {
                            _bookingResult.postValue(result)

                        } else {
                            _errorMessage.postValue(
                                result.message ?: "Booking failed. Please try again."
                            )
                        }
                    } ?: run {
                        _errorMessage.postValue("Server returned an empty response. Please try again.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue(parseErrorMessage(response.code(), errorBody))
                }

            } catch (e: IOException) {
                _errorMessage.postValue("No internet connection. Please check your network and try again.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error (${e.code()}): Unable to process your request.")
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun parseErrorMessage(statusCode: Int, errorBody: String?): String {
        return when (statusCode) {
            400 -> "Invalid request. Please check your input."
            401 -> "Unauthorized access. Please login again."
            403 -> "Your account is restricted. Contact support for assistance."
            404 -> "Server not found. Please try again later."
            500 -> "Oops! Something went wrong on our end. Please try again later."
            else -> errorBody ?: "Unexpected error occurred. Please try again."
        }
    }
}
