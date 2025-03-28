package com.example.groomers.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.groomers.R
import com.example.groomers.activity.AddAddress
import com.example.groomers.adapter.AddressListAdapter
import com.example.groomers.adapter.ClickEvent
import com.example.groomers.databinding.ActivityAddressListBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.model.modeladdresslist.Result
import com.example.groomers.retrofit.ApiService
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.LocationViewModel
import com.groomers.groomersvendor.helper.CustomLoader

class AddressList : AppCompatActivity(), ClickEvent {
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
        adapter = AddressListAdapter(emptyList(), this)
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
                Toastic.toastic(
                    context = this@AddressList,
                    message = "No addresses found",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.INFO,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
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

        // ✅ Observe delete status and refresh list after delete
        locationViewModel.modelDeleteAddAddress.observe(this) { isDeleted ->
            if (isDeleted.status == 1) {
                 Toastic.toastic(
                    context = this@AddressList,
                    message = "Address deleted successfully",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
                fetchAddressList() // Refresh list after successful delete
            }
        }
    }

    private fun fetchAddressList() {
        val token = sessionManager.accessToken
        if (token != null) {
            locationViewModel.getCustomerAddress(apiService, token)
        }
    }

    override fun edit(address: Result) {
        val intent = Intent(this, AddAddress::class.java)
        intent.putExtra("address", address.address)
        intent.putExtra("zipCode", address.zip_code.toString())
        intent.putExtra("city", address.city)
        intent.putExtra("state", address.name)
        intent.putExtra("country", address.countryname)
        intent.putExtra("id", address.id)
        startActivity(intent)
    }

    override fun delete(addressId: Int) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Delete Address")
            .setContentText("Are you sure you want to delete this address?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .setConfirmClickListener { dialog ->
                locationViewModel.deleteAddress(
                    apiService,
                    sessionManager.accessToken.toString(),
                    addressId
                )
                dialog.dismissWithAnimation()
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        fetchAddressList()
    }
}
