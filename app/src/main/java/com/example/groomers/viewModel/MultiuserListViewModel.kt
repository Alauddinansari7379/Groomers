package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelmultiuser.ModelMultiUser
import com.example.groomers.model.modelmultiuserlist.ModelMultiuserList
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MultiuserListViewModel
@Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _multiUserList = MutableLiveData<ModelMultiuserList?>()
    val multiuserList: LiveData<ModelMultiuserList?> get() = _multiUserList

    private val _multiUser = MutableLiveData<ModelMultiUser?>()
    val multiuser: LiveData<ModelMultiUser?> get() = _multiUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getAllVendorsByCategoryId() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            val token = sessionManager.mainAccessToken
            if (token.isNullOrEmpty()) {
                _errorMessage.postValue("Session expired. Please log in again.")
                _isLoading.postValue(false)
                return@launch
            }

            try {
                val response = apiService.getCustomerProfile("Bearer $token")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == 1) {
                        _multiUserList.postValue(body)
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

    fun createCustomerProfile(
        userName: String, name: String, password: String, userType: String,
        userImage: MultipartBody.Part?
    ) {
        _isLoading.postValue(true)

        viewModelScope.launch {
            val token = sessionManager.accessToken
            if (token.isNullOrEmpty()) {
                _errorMessage.postValue("Session expired. Please log in again.")
                _isLoading.postValue(false)
                return@launch
            }

            try {
                val response = apiService.createCustomerProfile("Bearer $token",userName,name,password,userType,userImage)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == 1) {
                        _multiUser.postValue(body)
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
