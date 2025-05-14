package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.groomers.model.modelvendorlists.ModelVendorsList
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VendorListViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _vendorList = MutableLiveData<ModelVendorsList?>()
    val vendorList: LiveData<ModelVendorsList?> get() = _vendorList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getAllVendorsByCategoryId(categoryId: String) {
        _isLoading.postValue(true)

        viewModelScope.launch {
            val token = sessionManager.accessToken
            if (token.isNullOrEmpty()) {
                _errorMessage.postValue("Session expired. Please log in again.")
                _isLoading.postValue(false)
                return@launch
            }

            try {
                val response = apiService.getAllVendorsByCategoryId(token, categoryId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == 1) {
                        _vendorList.postValue(body)
                    } else {
                        _errorMessage.postValue("No vendors found for this category.")
                    }
                } else {
                    val errorText = response.errorBody()?.string()
                    val userFriendlyMessage = getUserFriendlyError(response.code(), errorText)
                    _errorMessage.postValue(userFriendlyMessage)
                }

            } catch (e: IOException) {
                _errorMessage.postValue("You're offline. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Oops! Server error (${e.code()}). Please try again.")
            } catch (e: Exception) {
                _errorMessage.postValue("An unexpected error occurred. Please try again.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun getUserFriendlyError(statusCode: Int, errorBody: String?): String {
        return when (statusCode) {
            400 -> "Invalid request. Please try again."
            401 -> "Unauthorized. Please log in again."
            403 -> "Access denied. Please contact support."
            404 -> "Requested resource not found."
            500 -> "Something went wrong on our end. Please try later."
            else -> errorBody ?: "Unexpected error. Please try again."
        }
    }
}
