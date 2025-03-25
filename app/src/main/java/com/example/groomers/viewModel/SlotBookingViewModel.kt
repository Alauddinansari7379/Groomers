package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelslotbooking.ModelSlotBooking
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SlotBookingViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _slotBookingData = MutableLiveData<ModelSlotBooking?>()
    val slotBookingData: LiveData<ModelSlotBooking?> = _slotBookingData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchSlotBooking(vendorId: String, categoryId: String, day: String, serviceId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = apiService.getSlotBooking(
                    "Bearer ${sessionManager.accessToken}", vendorId, categoryId, day, serviceId
                )

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    if (responseBody?.status == 1) {
                        _slotBookingData.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("No available slots found for the selected service.")
                    }
                } else {
                    val errorMessage = parseErrorMessage(response.code(), response.errorBody()?.string())
                    _errorMessage.postValue(errorMessage)
                }
            } catch (e: IOException) {
                _errorMessage.postValue("No internet connection. Please check your network and try again.")
            } catch (e: HttpException) {
                _errorMessage.postValue("A server issue occurred. Please try again later.")
            } catch (e: Exception) {
                _errorMessage.postValue("Something went wrong. Please try again.${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun parseErrorMessage(statusCode: Int, errorBody: String?): String {
        return when (statusCode) {
            400 -> "Invalid request. Please check the details and try again."
            401 -> "Session expired. Please log in again."
            403 -> "Access denied. Contact support if you need help."
            404 -> "No slots available. Try selecting a different category or service."
            500 -> "Server is currently down. Please try again later."
            else -> errorBody ?: "An unexpected error occurred. Please try again."
        }
    }
}
