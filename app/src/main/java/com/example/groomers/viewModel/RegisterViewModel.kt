package com.example.groomers.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelregister.ModelRegesters
import com.example.groomers.retrofit.ApiService
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    var username: String? = null
    var name: String? = null
    var mobile: String? = null
    var email: String? = null
    var password: String? = null
    var password_confirmation: String? = null
    var role: String? = null
    var language: String? = null
    var user_type: String? = null
    var address: String? = null
    var country: String? = null
    var state: String? = null
    var city: String? = null
    var zipcode: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var gender: String? = null
    var birthdate: String? = null
//    var userImage: MultipartBody.Part? = null
    var userImage: String? =null


    private val _modelRegister = MutableLiveData<ModelRegesters?>()
    val modelRegister: MutableLiveData<ModelRegesters?> = _modelRegister

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(
        apiService: ApiService,
        userName: String,
        name: String,
        mobile: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        language: String,
        userType: String,
        address: String,
        country: String,
        state: String,
        city: String,
        zipCode: String,
        latitude: String,
        longitude: String,
        gender: String,
        birthDay: String,
        userImage: MultipartBody.Part

    ) {
        viewModelScope.launch {
            _isLoading.postValue(true) // Show loading state
            try {
                val response = apiService.registerUser(
                    userName,name, mobile, email, password, confirmPassword, role, language,
                    userType,  address, country, state, city, zipCode,
                     latitude, longitude, gender, birthDay,  userImage
                )

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status == 1) {
                            _modelRegister.postValue(body)

                        } else {
                            _errorMessage.postValue(body.message ?: "Registration unsuccessful. Please try again.")
                        }
                    } ?: run {
                        _errorMessage.postValue("Oops! Something went wrong. Please try again.")
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    _errorMessage.postValue("Couldn't complete registration. Please check your details and try again.")
                }
            } catch (e: Exception) { // Handle unexpected errors
                _errorMessage.postValue("Something went wrong. Please try again.")
                Log.e("RegisterViewModel", "Unexpected error: ${e.message}", e)
            } finally {
                _isLoading.postValue(false) // Hide loading state
            }
        }
    }

    fun clearData(){
        _modelRegister.value = null
        _errorMessage.value = null
    }

}
