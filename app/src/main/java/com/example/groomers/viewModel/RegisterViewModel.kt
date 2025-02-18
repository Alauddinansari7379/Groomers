package com.example.groomers.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.retrofit.ApiService
import com.groomers.groomersvendor.model.modelregister.ModelRegister
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
    var userImage: MultipartBody.Part? = null

    private val _modelRegister = MutableLiveData<ModelRegister>()
    val modelRegister: LiveData<ModelRegister> = _modelRegister

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(
        apiService: ApiService,
        username:String,name: String, mobile: String, email: String, password: String,
        passwordConfirmation: String, role: String, language: String,
        user_type: String, address: String, country: String,
        state: String, city: String, zipcode: String, latitude: String,
        longitude: String, gender: String, birthdate: String, userImage: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _isLoading.postValue(true) // Show loading state
            try {
                val response = apiService.registerUser(
                    username,name, mobile, email, password, passwordConfirmation, role, language,
                    user_type, address, country, state, city, zipcode, latitude,
                    longitude, gender, birthdate, userImage
                )

                _isLoading.postValue(false) // Hide loading state

                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        _modelRegister.postValue(response.body())
                    } else {
                        _errorMessage.postValue("Error: ${response.message()}")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _errorMessage.postValue("Exception: ${e.localizedMessage}")
                Log.e("RegisterViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}
