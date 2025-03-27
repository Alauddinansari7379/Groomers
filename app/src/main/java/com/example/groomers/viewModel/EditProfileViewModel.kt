package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelbooking.ModelBooking
import com.example.groomers.model.modeleditprofile.ModelEditProfile
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _modelEditProfile = MutableLiveData<ModelEditProfile?>()
    val modelEditProfile: LiveData<ModelEditProfile?> = _modelEditProfile

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Update User Profile Details
     */
    fun updateUserDetails(
        token: String,
        name: String,
        mobile: String,
        email: String,
        address: String,
        gender: String,
        birthdate: String
    ) {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                // API Call to update profile details
                val response = apiService.updateUserDetails(
                    "Bearer $token", name, mobile, email, address, gender, birthdate
                )

                // Handle Response
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        if (result.status == 1) {
                            _modelEditProfile.postValue(result)
                        } else {
                            _errorMessage.postValue(
                                result.message ?: "Profile update failed. Please try again."
                            )
                        }
                    } ?: run {
                        _errorMessage.postValue("No response from server. Please try again later.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue(parseErrorMessage(response.code(), errorBody))
                }
            } catch (e: IOException) {
                _errorMessage.postValue("No internet connection. Please check your network.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error (${e.code()}): Please try again later.")
            } catch (e: Exception) {
                _errorMessage.postValue("Oops! Something went wrong. Please try again.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Parse and Return User-Friendly Error Messages
     */
    private fun parseErrorMessage(statusCode: Int, errorBody: String?): String {
        return when (statusCode) {
            400 -> "Invalid request. Please check your inputs and try again."
            401 -> "Unauthorized access. Please log in again."
            403 -> "Your account has restricted access. Contact support if needed."
            404 -> "Server not found. Please try again later."
            500 -> "Server error. Please wait a moment and try again."
            else -> errorBody ?: "An unexpected error occurred. Please try again."
        }
    }
}
