package com.example.groomers.viewModel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modellogin.ModelLogin
import com.example.groomers.model.modeluserdetails.ModelUserDetails
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application)  {

    private val _modelLogin = MutableLiveData<ModelLogin?>()
    val modelLogin: LiveData<ModelLogin?> = _modelLogin

    private val _modelUserDetails = MutableLiveData<ModelUserDetails?>()
    val modelUserDetails: LiveData<ModelUserDetails?> = _modelUserDetails

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String,username:String,password: String) {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val response = apiService.login(email, password,username,"user")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    if (!responseBody?.access_token.isNullOrEmpty()) {
                        _modelLogin.postValue(responseBody)
                        sessionManager.accessToken = responseBody!!.access_token
                        sessionManager.mobile = responseBody!!.user.mobile
                        sessionManager.email = responseBody!!.user.email
                        sessionManager.name = responseBody!!.user.name
                        sessionManager.username = responseBody!!.user.username
                       // sessionManager.accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2dyb29tZXJzLmNvLmluL2FwaS9sb2dpbiIsImlhdCI6MTczOTk2NjkwMywiZXhwIjoxNzQxMjYyOTAzLCJuYmYiOjE3Mzk5NjY5MDMsImp0aSI6IllSdkEyQmFVOU5Gbm0yTzQiLCJzdWIiOiI1MiIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.W0Lr0OYWvznJfvJJ-rF82UcWmXzkaXqYrCXoy1YjLE0"
                        sessionManager.isLogin = true
                    } else {
                        _errorMessage.postValue("Incorrect email or password. Please try again.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(response.code(), errorBody)
                    _errorMessage.postValue(errorMessage)
                }
            } catch (e: IOException) {
                // Network-related error (e.g., no internet connection)
                _errorMessage.postValue("No internet connection. Please check your network and try again.")
            } catch (e: HttpException) {
                // Handle HTTP-specific errors
                _errorMessage.postValue("Server error (${e.code()}): Unable to process your request. Please try again later.")
            } catch (e: Exception) {
                _errorMessage.postValue("Something went wrong. Please try again later.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getUserDetails() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val response = apiService.getUserDetails("Bearer ${sessionManager.accessToken}")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    if (responseBody?.status == 1) {
                        _modelUserDetails.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("Oops! Something went wrong. Please try again.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(response.code(), errorBody)
                    _errorMessage.postValue(errorMessage)
                }
            } catch (e: IOException) {
                // No internet or network issues
                _errorMessage.postValue("No internet connection. Please check your network and try again.")
            } catch (e: HttpException) {
                // Handle HTTP-specific errors
                _errorMessage.postValue("Server issue detected. Please try again in a few moments.")
            } catch (e: Exception) {
                // Catch-all for unexpected errors
                _errorMessage.postValue("Something went wrong. We're working on it! Please try again shortly.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    private fun parseErrorMessage(statusCode: Int, errorBody: String?): String {
        return when (statusCode) {
            400 -> "Invalid request. Please check your input."
            401 -> "Unauthorized access. Please check your email and password."
            403 -> "Your account is restricted. Contact support for assistance."
            404 -> "Server not found. Please try again later."
            500 -> "Internal server error. Please try again later."
            else -> errorBody ?: "Unexpected error occurred. Please try again."
        }
    }

}
