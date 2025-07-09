package com.example.groomers.activity


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groomers.adapter.VendorsListAdapter
import com.example.groomers.databinding.ActivityShowVendorsBinding
import com.example.groomers.fragments.HomeFragment.Companion.userId
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.VendorListViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowVendors : AppCompatActivity() {

    private val viewModel: VendorListViewModel by viewModels()
    private lateinit var adapter: VendorsListAdapter

    @Inject
    lateinit var sessionManager: SessionManager

    private val context = this@ShowVendors
    private val binding by lazy { ActivityShowVendorsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val categoryId = intent.getStringExtra("category_id")
        if (categoryId.isNullOrEmpty()) {
            Toastic.toastic(
                context = this@ShowVendors,
                message = "Invalid category ID",
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.ERROR,
                isIconAnimated = true,
                textColor = Color.RED,
            ).show()
            finish()
            return
        }

        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.getAllVendorsByCategoryId(categoryId)

        viewModel.isLoading.observe(context) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(context)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        viewModel.vendorList.observe(context) { modelVendors ->
            modelVendors?.let {
                if (modelVendors.status == 1) {
                    if (modelVendors.result.isNotEmpty()) {
                        binding.tvNoDataFound.visibility = View.GONE
                        binding.rvVendorList.visibility = View.VISIBLE
                        adapter.updateData(modelVendors.result)
                    } else {
                        binding.tvNoDataFound.visibility = View.VISIBLE
                        binding.rvVendorList.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.errorMessage.observe(context) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toastic.toastic(
                    context = this@ShowVendors,
                    message = errorMessage,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = Color.BLUE,
                ).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = VendorsListAdapter(emptyList()) { selectedService ->
            userId = selectedService.user_id.toString()
            val intent = Intent(this@ShowVendors, BookingDetail::class.java).apply {
                putExtra("service_name", selectedService.name)
                putExtra("service_image", selectedService.profile_picture) // Replace if image needed
                putExtra("service_description", selectedService.aboutBusiness)
                putExtra("service_type", selectedService.services)
                putExtra("service_address", selectedService.address)
                putExtra("vendorId", selectedService.user_id)
                putExtra("serviceId", selectedService.user_id)
                putExtra("service_price", selectedService.teamSize.toString())
            }
            Log.d("ShowVendors", "Navigating to BookingDetail with: ${selectedService.name}")
            startActivity(intent)
        }

        binding.rvVendorList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@ShowVendors.adapter
        }
    }

}
