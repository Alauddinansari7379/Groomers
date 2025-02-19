package com.example.groomers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getServiceList(accessToken: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = apiService.getServiceList("Bearer $accessToken")
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


}
