package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelbookinglist.ModelBookingList
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
@HiltViewModel
class BookingListViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _bookingList = MutableLiveData<ModelBookingList?>()
    val bookingList: LiveData<ModelBookingList?> = _bookingList

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchBookingList(token: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = apiService.getBookingList("Bearer $token")

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.status == 1) {
                        _bookingList.value = result
                    } else {
                        _errorMessage.value = result?.message ?: "Failed to load bookings. Please try again."
                    }
                } else {
                    _errorMessage.value = parseErrorMessage(response.code(), response.errorBody()?.string())
                }

            } catch (e: IOException) {
                _errorMessage.value = "No internet connection. Please check your network."
            } catch (e: HttpException) {
                _errorMessage.value = "Server error (${e.code()}): Please try again later."
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again($e)."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseErrorMessage(statusCode: Int, errorBody: String?): String {
        return when (statusCode) {
            400 -> "Invalid request. Please check your input."
            401 -> "Session expired. Please log in again."
            403 -> "Access restricted. Contact support for assistance."
            404 -> "Data not found. Please try again later."
            500 -> "Oops! Something went wrong on our end."
            else -> errorBody ?: "Unexpected error occurred. Please try again."
        }
    }
}
