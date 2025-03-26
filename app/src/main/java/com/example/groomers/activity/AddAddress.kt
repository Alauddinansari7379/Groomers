package com.example.groomers.activity


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityAddAddressBinding
import com.example.groomers.retrofit.ApiService
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.LocationViewModel
import com.groomers.groomersvendor.helper.CustomLoader

class AddAddress : AppCompatActivity() {
    private val locationViewModel: LocationViewModel by viewModels()
    private val binding by lazy { ActivityAddAddressBinding.inflate(layoutInflater) }
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager

    private var countryIdMap: Map<String, Int> = emptyMap()
    private var stateIdMap: Map<String, Int> = emptyMap()
    private var cityIdMap: Map<String, Int> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiService = ApiServiceProvider.getApiService()

        locationViewModel.getCountry(apiService)
        locationViewModel.getState(apiService)
        locationViewModel.getCity(apiService)

        observeViewModel()
        binding.btnSubmit.setOnClickListener { submitAddress() }
    }

    private fun observeViewModel() {
        locationViewModel.modelCountry.observe(this) { response ->
            response?.result?.let { countryList ->
                countryIdMap = countryList.associateBy({ it.name }, { it.id })
                val countries = listOf("Select Country") + countryIdMap.keys
                setupCountrySpinner(countries)
            }
        }

        locationViewModel.modelState.observe(this) { response ->
            response?.result?.let { stateList ->
                stateIdMap = stateList.associateBy({ it.name }, { it.id })
                val states = listOf("Select State") + stateIdMap.keys
                setupStateSpinner(states)
            }
        }

        locationViewModel.modelCity.observe(this) { response ->
            response?.result?.let { cityList ->
                cityIdMap = cityList.associateBy({ it.city }, { it.id })
                val cities = listOf("Select City") + cityIdMap.keys
                setupCitySpinner(cities)
            }
        }

        locationViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(this)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        locationViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        locationViewModel.modelAddAddress.observe(this) { response ->
            if (response != null && response.status == 1) {
                Toast.makeText(this, "Address added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCountrySpinner(countries: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries)
        binding.spinnerCountry.adapter = adapter
    }

    private fun setupStateSpinner(states: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, states)
        binding.spinnerState.adapter = adapter
    }

    private fun setupCitySpinner(cities: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        binding.spinnerCity.adapter = adapter
    }

    private fun submitAddress() {
        val address = binding.etAddress.text.toString().trim()
        val zipCode = binding.etZipCode.text.toString().trim()
        val countryName = binding.spinnerCountry.selectedItem.toString()
        val stateName = binding.spinnerState.selectedItem.toString()
        val cityName = binding.spinnerCity.selectedItem.toString()

        val countryId = countryIdMap[countryName] ?: -1
        val stateId = stateIdMap[stateName] ?: -1
        val cityId = cityIdMap[cityName] ?: -1

        if (address.isEmpty() || zipCode.isEmpty() || countryId == -1 || stateId == -1 || cityId == -1) {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sessionManager.accessToken
        if (token != null) {
            locationViewModel.addAddress(apiService, token, address, zipCode, countryId, stateId, cityId)
        }
    }
}
