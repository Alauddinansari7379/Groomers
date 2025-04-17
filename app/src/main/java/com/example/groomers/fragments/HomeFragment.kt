package com.example.groomers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail
import com.example.groomers.adapter.CategoryAdapter
import com.example.groomers.adapter.ServiceAdapter
import com.example.groomers.adapter.SliderAdapter
import com.example.groomers.databinding.FragmentHomeUserBinding
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.CategoryViewModel
import com.example.groomers.viewModel.LoginViewModel
import com.example.groomers.viewModel.ServiceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home_user) {

    private lateinit var binding: FragmentHomeUserBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var sliderAdapter: SliderAdapter
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val viewModel1: LoginViewModel by viewModels()
    private val viewModel: ServiceViewModel by viewModels()

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 100
    private var currentAddress = ""
    private var currentPage = 0
    private var timer: Timer? = null

    @Inject
    lateinit var sessionManager: SessionManager

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
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()

        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
                viewModel.getServiceList(token, "Male")
                viewModel1.getUserDetails()
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Missing Token", Toast.LENGTH_LONG).show()
        }

        setupSlider(emptyList()) // Initialize slider with empty list
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(emptyList()) { selectedService ->
            val intent = Intent(requireContext(), BookingDetail::class.java).apply {
                putExtra("service_name", selectedService.serviceName)
                putExtra("service_image", selectedService.image)
                putExtra("service_description", selectedService.description)
                putExtra("service_type", selectedService.serviceType)
                putExtra("service_address", selectedService.address)
                putExtra("vendorId", selectedService.user_id)
                putExtra("serviceId", selectedService.id)
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

    private fun setupSlider(imageUrls: List<String>) {
        sliderAdapter = SliderAdapter(imageUrls)
        binding.imageService.adapter = sliderAdapter

        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            if (currentPage == imageUrls.size) {
                currentPage = 0
            }
            binding.imageService.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3000, 3000)

        binding.imageService.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }
        })
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(requireContext())
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.modelService.observe(viewLifecycleOwner) { response ->
            response?.result?.let { services ->
                serviceAdapter.updateData(services)
                val imageUrls = services.map { it.image } // Extract image URLs for slider
                setupSlider(imageUrls) // Update slider with images
            } ?: run {
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel1.modelUserDetails.observe(viewLifecycleOwner) { response ->
            response?.result?.firstOrNull()?.let { userDetail ->
                sessionManager.username = userDetail.name
                sessionManager.profilePictureUrl = userDetail.profile_picture
                sessionManager.userType = userDetail.user_type
            } ?: run {
                Toast.makeText(requireContext(), "No data available.", Toast.LENGTH_SHORT).show()
            }
        }

        categoryViewModel.modelCategory.observe(requireActivity()) { modelCategory ->
            binding.rvCategory1.apply {
                adapter = CategoryAdapter(modelCategory.result, requireActivity())
            }
        }

        categoryViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(context)
            else CustomLoader.hideLoaderDialog()
        }

        categoryViewModel.errorMessage.observe(requireActivity()) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            currentAddress = addresses?.get(0)?.getAddressLine(0).toString()

                            binding.locationText.text = sessionManager.name
                            binding.subLocationText.text = addresses?.get(0)?.locality
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
        } else {
            askPermission()
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }
}
