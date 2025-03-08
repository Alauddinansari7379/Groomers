package com.example.groomers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail
import com.example.groomers.adapter.CategoryAdapter
import com.example.groomers.adapter.ServiceAdapter
import com.example.groomers.databinding.FragmentHomeUserBinding
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.CategoryViewModel
import com.example.groomers.viewModel.ServiceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home_user) {

    private lateinit var binding: FragmentHomeUserBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private val categoryViewModel: CategoryViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: ServiceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val apiService = ApiServiceProvider.getApiService()
        categoryViewModel.getCategory(apiService)
        setupRecyclerView()
        observeViewModel()
        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
//                viewModel.getServiceList(token, sessionManager.userType.toString())
                viewModel.getServiceList(token, "Female")
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Missing Token", Toast.LENGTH_LONG).show()
        }
    }


    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(emptyList()) { selectedService ->
            val intent = Intent(requireContext(), BookingDetail::class.java).apply {
                putExtra("service_name", selectedService.serviceName)
                putExtra("service_image", selectedService.image)
                putExtra("service_description", selectedService.description)
                putExtra("service_type", selectedService.serviceType)
                putExtra("service_address", selectedService.address)
                putExtra("service_price", selectedService.price.toString())
            }
            Log.d(
                "HomeFragment",
                "Navigating to BookingDetail with: ${selectedService.serviceName}"
            )
            startActivity(intent)
        }

        binding.rvHorizontalList.adapter = serviceAdapter
    }


    private fun observeViewModel() {
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(requireContext())
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.modelService.observe(viewLifecycleOwner) { response ->
            response?.result?.let { services ->
                serviceAdapter.updateData(services) // Update adapter data instead of reinitializing
            } ?: run {
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
            }
        }

        categoryViewModel.modelCategory.observe(requireActivity()) { modelCategory ->
            val gridLayoutManager =
                GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false)
            binding.rvCategory1.layoutManager = gridLayoutManager
            binding.rvCategory1.adapter = CategoryAdapter(modelCategory.result, requireActivity())

        }
        categoryViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(context)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }
        // Observe error message if login fails
        categoryViewModel.errorMessage.observe(requireActivity()) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

}

