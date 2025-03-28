package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groomers.model.modeladdresslist.ModelAddressList
import com.example.groomers.model.modelcreateaddress.ModelCreateAddress
import com.example.groomers.model.modeldeleteaddress.ModelDeleteAddress
import com.example.groomers.model.modelupdateaddress.ModelUpdateAddress
import com.example.groomers.model.modulcountry.ModelCountry
import com.example.groomers.retrofit.ApiService
import com.groomers.groomersvendor.model.modelcity.ModelCity
import com.groomers.groomersvendor.model.modelstate.ModelState
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val _modelCity = MutableLiveData<ModelCity>()
    val modelCity: LiveData<ModelCity> = _modelCity

    private val _modelCountry = MutableLiveData<ModelCountry>()
    val modelCountry: LiveData<ModelCountry> = _modelCountry

    private val _modelState = MutableLiveData<ModelState>()
    val modelState: LiveData<ModelState> = _modelState

    private val _modelAddressList = MutableLiveData<ModelAddressList>()
    val modelAddressList: LiveData<ModelAddressList> = _modelAddressList

    private val _modelAddAddress = MutableLiveData<ModelCreateAddress>()
    val modelAddAddress: LiveData<ModelCreateAddress> = _modelAddAddress

    private val _modelUpdateAddAddress = MutableLiveData<ModelUpdateAddress>()
    val modelUpdateAddAddress: LiveData<ModelUpdateAddress> = _modelUpdateAddAddress

    private val _modelDeleteAddAddress = MutableLiveData<ModelDeleteAddress>()
    val modelDeleteAddAddress: LiveData<ModelDeleteAddress> = _modelDeleteAddAddress

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getCity(apiService: ApiService) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getCity()
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.status == 1) {
                        _modelCity.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("Error: ${response.message()}")
                    }
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getState(apiService: ApiService) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getState()
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.status == 1) {
                        _modelState.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("Error: ${response.message()}")
                    }
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getCountry(apiService: ApiService) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getCountry()
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.status == 1) {
                        _modelCountry.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("Error: ${response.message()}")
                    }
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addAddress(
        apiService: ApiService,
        token: String,
        address: String,
        zipCode: String,
        country: Int,
        state: Int,
        city: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.customerCreateAddress(
                    "Bearer $token",
                    address,
                    zipCode,
                    country,
                    state,
                    city
                )
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.status == 1) {
                        _modelAddAddress.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("Error: ${response.message()}")
                    }
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getCustomerAddress(apiService: ApiService, token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getCustomerAddress("Bearer $token")
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    _modelAddressList.postValue(response.body())
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun updateAddress(
        apiService: ApiService,
        token: String,
        id: Int,
        address: String,
        zipCode: String,
        country: String,
        state: String,
        city: String
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.updateAddress(
                    "Bearer $token",
                    address,
                    zipCode,
                    country,
                    state,
                    city,
                    id
                )
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.status == 1) {
                        _modelUpdateAddAddress.postValue(responseBody)
                    } else {
                        _errorMessage.postValue("Error: ${responseBody.message}")
                    }
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteAddress(apiService: ApiService, token: String, id: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.deleteAddress("Bearer $token", id)
                _isLoading.postValue(false)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.status == 1) {
                        _modelDeleteAddAddress.postValue(responseBody) // Handle response if needed
                    } else {
                        _errorMessage.postValue("Error: ${responseBody.message}")
                    }
                } else {
                    _errorMessage.postValue("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                _errorMessage.postValue("Server error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}