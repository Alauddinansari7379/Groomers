package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.groomers.adapter.AllVendorsAdapter
import com.example.groomers.adapter.CategoryAdapter
import com.example.groomers.adapter.ServiceAdapter
import com.example.groomers.databinding.ActivityViewAllBinding
import com.example.groomers.fragments.HomeFragment.Companion.userId
import com.example.groomers.fragments.HomeFragment.Companion.viewAllClick
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.CategoryViewModel
import com.example.groomers.viewModel.LoginViewModel
import com.example.groomers.viewModel.ServiceViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ViewAllActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewAllBinding.inflate(layoutInflater) }

    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel1: LoginViewModel by viewModels()
    private val viewModel: ServiceViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    private var originalCategoryList: List<com.example.groomers.model.modelcategory.Result> =
        emptyList()
    private var originalServiceList: List<com.example.groomers.model.modelservice.Result> =
        emptyList()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val token = sessionManager.accessToken
        if (token != null) {
            lifecycleScope.launch {
                viewModel1.getUserDetails()
                if (!sessionManager.userType.isNullOrEmpty()) {
                    viewModel.getServiceList(token, sessionManager.userType.toString())
                }
                viewModel.getAllVendors(token)

                // 🚀 Fetch categories here
                val apiService = ApiServiceProvider.getApiService()
                categoryViewModel.getCategory(apiService)
            }
        } else {
            Toast.makeText(this, "Error: Missing Token", Toast.LENGTH_LONG).show()
        }

        Log.d("ViewAllActivity", "viewAllClick: $viewAllClick")
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        if (viewAllClick == "Offer") {
            observeViewModelOffer()
            binding.appCompatTextView2.text = "Special Offers"
        } else {
            observeViewModelService()
            binding.appCompatTextView2.text = "Our Service"

        }
    }

    private fun observeViewModelOffer() {
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this@ViewAllActivity, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel1.isLoading.observe(this) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(this@ViewAllActivity)
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.modelService.observe(this) { response ->
            response?.result?.let { services ->
                originalServiceList = services
                serviceAdapter.updateData(services)
            } ?: run {
                Toast.makeText(this@ViewAllActivity, "No data available", Toast.LENGTH_SHORT).show()
            }
        }

        val gridLayoutManager = GridLayoutManager(this, 1)
        binding.rvHorizontalVendorList.layoutManager = gridLayoutManager

        serviceAdapter = ServiceAdapter(emptyList()) { selectedService ->
            userId = selectedService.user_id.toString()
            val intent = Intent(this, BookingDetail::class.java).apply {
                putExtra("service_name", selectedService.serviceName)
                putExtra("service_image", selectedService.image)
                putExtra("service_description", selectedService.description)
                putExtra("service_type", selectedService.serviceType)
                putExtra("service_address", selectedService.address)
                putExtra("vendorId", selectedService.user_id)
                putExtra("serviceId", selectedService.id)
                putExtra("aboutBusiness", selectedService.aboutBusiness)
                putExtra("overall_ratings", selectedService.overall_ratings)
                putExtra("no_of_ratings", selectedService.no_of_ratings)
                putExtra("service_price", selectedService.price.toString())
            }
            startActivity(intent)
        }

        binding.rvHorizontalVendorList.adapter = serviceAdapter
    }

    private fun observeViewModelService() {
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this@ViewAllActivity, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel1.isLoading.observe(this) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(this@ViewAllActivity)
            else CustomLoader.hideLoaderDialog()
        }

        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.rvCategory1.layoutManager = gridLayoutManager

        categoryViewModel.modelCategory.observe(this) { modelCategory ->
            modelCategory?.let {
                originalCategoryList = it.result
                Log.d("ViewAllActivity", "Category Data Received: ${it.result.size} items")

                categoryAdapter = CategoryAdapter(originalCategoryList) { selectedCategory ->
                    userId = selectedCategory.id.toString()
                    val intent = Intent(this, ShowVendors::class.java).apply {
                        putExtra("category_id", selectedCategory.id.toString())
                        putExtra("category_name", selectedCategory.category_name)
                    }
                    startActivity(intent)
                }

                binding.rvCategory1.adapter = categoryAdapter
            } ?: run {
                Toast.makeText(this@ViewAllActivity, "No categories found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
