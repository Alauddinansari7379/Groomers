package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groomers.R
import com.example.groomers.adapter.AddressListAdapter
import com.example.groomers.databinding.ActivityAddressListBinding
import com.example.groomers.retrofit.ApiService
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.LocationViewModel
import com.groomers.groomersvendor.helper.CustomLoader


class AddressList : AppCompatActivity() {
    private val binding by lazy { ActivityAddressListBinding.inflate(layoutInflater) }
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: AddressListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiService = ApiServiceProvider.getApiService()

        setupRecyclerView()

        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this@AddressList, AddAddress::class.java))
        }

        observeViewModel()
        fetchAddressList()
    }

    private fun setupRecyclerView() {
        adapter = AddressListAdapter(emptyList())
        binding.recyclerViewAddresses.apply {
            layoutManager = LinearLayoutManager(this@AddressList)
            adapter = this@AddressList.adapter
        }
    }

    private fun observeViewModel() {
        locationViewModel.modelAddressList.observe(this) { response ->
            if (response != null && response.status == 1) {
                adapter.updateList(response.result)
            } else {
                Toast.makeText(this, "No addresses found", Toast.LENGTH_SHORT).show()
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
    }

    private fun fetchAddressList() {
        val token = sessionManager.accessToken
        if (token != null) {
            locationViewModel.getCustomerAddress(apiService, token)
        }
    }
}