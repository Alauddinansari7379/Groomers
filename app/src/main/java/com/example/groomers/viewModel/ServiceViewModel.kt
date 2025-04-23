package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modelallvendors.ModelAllVendors
import com.example.groomers.model.modelgetallvendorbyid.ModelAllPostByVendorId
import com.example.groomers.model.modelservice.ModelService
import com.example.groomers.retrofit.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application
) : AndroidViewModel(application) {

    private val _modelService = MutableLiveData<ModelService>()
    val modelService: LiveData<ModelService?> = _modelService

    private val _modelAllVendors = MutableLiveData<ModelAllVendors>()
    val modelAllVendors: LiveData<ModelAllVendors?> = _modelAllVendors

    private val _modelAllPostByVendorId = MutableLiveData<ModelService>()
    val modelAllPostByVendorId: LiveData<ModelService?> = _modelAllPostByVendorId

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getServiceList(accessToken: String,userType : String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getServiceList("Bearer $accessToken",userType)
                _isLoading.postValue(false)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status == 1) {
                            _modelService.postValue(body)
                        } else {
                            _errorMessage.postValue(body.message ?: "Failed to fetch services. Please try again.")
                        }
                    } ?: run {
                        _errorMessage.postValue("Unexpected response from the server.")
                    }
                } else {
                    _errorMessage.postValue("Failed to load services. Please try again later.")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("No internet connection. Please check your network.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error. Please try again later.")
            } catch (e: Exception) {
                _errorMessage.postValue("Something went wrong. Please try again.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


    fun getAllVendors(accessToken: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getAllVendors("Bearer $accessToken")
                _isLoading.postValue(false)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status == 1) {
                            _modelAllVendors.postValue(body)
                        } else {
                            _errorMessage.postValue(body.message ?: "Failed to fetch services. Please try again.")
                        }
                    } ?: run {
                        _errorMessage.postValue("Unexpected response from the server.")
                    }
                } else {
                    _errorMessage.postValue("Failed to load services. Please try again later.")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("No internet connection. Please check your network.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error. Please try again later.")
            } catch (e: Exception) {
                _errorMessage.postValue("Something went wrong. Please try again.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getServiceListByVendorId(accessToken: String,id : Int) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getAllPostByVendorId("Bearer $accessToken",id)
                _isLoading.postValue(false)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status == 1) {
                            _modelAllPostByVendorId.postValue(body)
                        } else {
                            _errorMessage.postValue(body.message ?: "Failed to fetch services. Please try again.")
                        }
                    } ?: run {
                        _errorMessage.postValue("Unexpected response from the server.")
                    }
                } else {
                    _errorMessage.postValue("Failed to load services. Please try again later.")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("No internet connection. Please check your network.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error. Please try again later.")
            } catch (e: Exception) {
                _errorMessage.postValue("Something went wrong. Please try again.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}
