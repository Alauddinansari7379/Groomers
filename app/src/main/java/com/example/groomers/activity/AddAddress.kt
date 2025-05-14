package com.example.groomers.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityAddAddressBinding
import com.example.groomers.helper.Toastic
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

    private var addressId: Int? = null // ✅ For updating existing address
    private var selectedCountryId: Int? = null
    private var selectedStateId: Int? = null
    private var selectedCityId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiService = ApiServiceProvider.getApiService()

        // ✅ Check if we are editing an existing address
        getIntentData()

        // ✅ Initial API calls
        locationViewModel.getCountry(apiService)
        locationViewModel.getState(apiService) // Initial call to load states
        locationViewModel.getCity(apiService)  // Initial call to load cities

        observeViewModel()
        setupListeners()
        binding.btnSubmit.setOnClickListener { submitAddress() }
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    // ✅ Get data if we are editing an address
    private fun getIntentData() {
        intent?.let {
            addressId = it.getIntExtra("id", -1)
            val address = it.getStringExtra("address")
            val zipCode = it.getStringExtra("zipCode")
            selectedCountryId = it.getIntExtra("countryId", -1)
            selectedStateId = it.getIntExtra("stateId", -1)
            selectedCityId = it.getIntExtra("cityId", -1)

            if (addressId != -1 && address != null) {
                binding.etAddress.setText(address)
                binding.etZipCode.setText(zipCode)
                binding.btnSubmit.text = "Update Address" // Change button text
            }
        }
    }

    private fun observeViewModel() {
        locationViewModel.modelCountry.observe(this) { response ->
            response?.result?.let { countryList ->
                countryIdMap = countryList.associateBy({ it.name }, { it.id })
                val countries = listOf("Select Country") + countryIdMap.keys
                setupCountrySpinner(countries)

                // ✅ Set country selection if editing address
                if (selectedCountryId != null && selectedCountryId != -1) {
                    val selectedCountryName = countryIdMap.entries.find { it.value == selectedCountryId }?.key
                    if (selectedCountryName != null) {
                        binding.spinnerCountry.postDelayed({
                            binding.spinnerCountry.setSelection(getSpinnerPosition(binding.spinnerCountry, selectedCountryName))
                        }, 200)
                    }
                }
            }
        }

        locationViewModel.modelState.observe(this) { response ->
            response?.result?.let { stateList ->
                stateIdMap = stateList.associateBy({ it.name }, { it.id })
                val states = listOf("Select State") + stateIdMap.keys
                setupStateSpinner(states)

                // ✅ Set state selection if editing address
                if (selectedStateId != null && selectedStateId != -1) {
                    val selectedStateName = stateIdMap.entries.find { it.value == selectedStateId }?.key
                    if (selectedStateName != null) {
                        binding.spinnerState.postDelayed({
                            binding.spinnerState.setSelection(getSpinnerPosition(binding.spinnerState, selectedStateName))
                        }, 200)
                    }
                }
            }
        }

        locationViewModel.modelCity.observe(this) { response ->
            response?.result?.let { cityList ->
                cityIdMap = cityList.associateBy({ it.city }, { it.id })
                val cities = listOf("Select City") + cityIdMap.keys
                setupCitySpinner(cities)

                // ✅ Set city selection if editing address
                if (selectedCityId != null && selectedCityId != -1) {
                    val selectedCityName = cityIdMap.entries.find { it.value == selectedCityId }?.key
                    if (selectedCityName != null) {
                        binding.spinnerCity.postDelayed({
                            binding.spinnerCity.setSelection(getSpinnerPosition(binding.spinnerCity, selectedCityName))
                        }, 200)
                    }
                }
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
                Toastic.toastic(
                    context = this@AddAddress,
                    message = errorMessage,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }

        // ✅ Observe add address response
        locationViewModel.modelAddAddress.observe(this) { response ->
            if (response != null && response.status == 1 && (addressId == null || addressId == -1)) {
                Toastic.toastic(
                    context = this@AddAddress,
                    message = "Address added successfully!",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
                setResult(Activity.RESULT_OK) // ✅ Notify parent to refresh list
                finish()
            }
        }

        // ✅ Observe update address response using modelUpdateAddAddress
        locationViewModel.modelUpdateAddAddress.observe(this) { response ->
            if (response != null && response.status == 1 && addressId != null && addressId != -1) {
                Toastic.toastic(
                    context = this@AddAddress,
                    message = "Address updated successfully!",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
                setResult(Activity.RESULT_OK) // ✅ Notify parent to refresh list
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val countryName = binding.spinnerCountry.selectedItem.toString()
                val countryId = countryIdMap[countryName]
                if (countryId != null && countryId != -1) {
                    selectedCountryId = countryId
                    locationViewModel.getState(apiService) // ✅ Load states for selected country
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val stateName = binding.spinnerState.selectedItem.toString()
                val stateId = stateIdMap[stateName]
                if (stateId != null && stateId != -1) {
                    selectedStateId = stateId
                    locationViewModel.getCity(apiService) // ✅ Load cities for selected state
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun getSpinnerPosition(spinner: Spinner, value: String?): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                return i
            }
        }
        return 0
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
            Toastic.toastic(
                context = this@AddAddress,
                message = "Please fill in all fields correctly",
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.WARNING,
                isIconAnimated = true,
                textColor = if (false) Color.BLUE else null,
            ).show()
            return
        }

        val token = sessionManager.accessToken
        if (token != null) {
            if (addressId == null || addressId == -1) {
                // ✅ Add address if addressId is null or -1
                locationViewModel.addAddress(apiService, token, address, zipCode, countryId, stateId, cityId)
            } else {
                // ✅ Update address if addressId is valid
                locationViewModel.updateAddress(
                    apiService,
                    token,
                    addressId!!,
                    address,
                    zipCode,
                    countryId.toString(),
                    stateId.toString(),
                    cityId.toString()
                )
            }
        }
    }
}
