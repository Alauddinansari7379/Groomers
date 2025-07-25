package com.example.groomers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail
import com.example.groomers.activity.ShowVendors
import com.example.groomers.adapter.AllVendorsAdapter
import com.example.groomers.adapter.CategoryAdapter
import com.example.groomers.adapter.ServiceAdapter
import com.example.groomers.adapter.SliderAdapter
import com.example.groomers.databinding.FragmentHomeUserBinding
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.CategoryViewModel
import com.example.groomers.viewModel.LoginViewModel
import com.example.groomers.viewModel.ServiceViewModel
import com.example.groomers.viewModel.SharedViewModel
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
    private lateinit var allVendorsAdapter: AllVendorsAdapter
    private lateinit var sliderAdapter: SliderAdapter
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val viewModel1: LoginViewModel by viewModels()
    private val viewModel: ServiceViewModel by viewModels()
    private val shareViewModel: SharedViewModel by activityViewModels()

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 100
    private var currentAddress = ""
    private var currentPage = 0
    private var timer: Timer? = null

    @Inject
    lateinit var sessionManager: SessionManager

    private var originalCategoryList: List<com.example.groomers.model.modelcategory.Result> =
        emptyList()
    private var originalServiceList: List<com.example.groomers.model.modelservice.Result> =
        emptyList()


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
        lifecycleScope.launch {
            viewModel1.getUserDetails()
        }
        val apiService = ApiServiceProvider.getApiService()
        categoryViewModel.getCategory(apiService)
        setupRecyclerView()
        observeViewModel()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()

        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
                viewModel1.getUserDetails()
                if (!sessionManager.userType.isNullOrEmpty()) {
                    viewModel.getServiceList(token, sessionManager.userType.toString())
                }
                viewModel.getAllVendors(token)
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Missing Token", Toast.LENGTH_LONG).show()
        }

        setupSlider(emptyList()) // Initialize slider with empty list

        setupSearchBar()
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(emptyList()) { selectedService ->
            userId = selectedService.user_id.toString()
            val intent = Intent(requireContext(), BookingDetail::class.java).apply {
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
//                putExtra("rating", selectedService.rating.toString())
                putExtra("service_price", selectedService.price.toString())
            }
            Log.d(
                "HomeFragment",
                "Navigating to BookingDetail with: ${selectedService.serviceName}"
            )
            startActivity(intent)
        }
        binding.rvHorizontalList.adapter = serviceAdapter

        allVendorsAdapter = AllVendorsAdapter(emptyList()) { selectedService ->
            userId = selectedService.user_id.toString()
            val intent = Intent(requireContext(), BookingDetail::class.java).apply {
                putExtra("service_name", selectedService.name)
                putExtra("service_image", selectedService.profile_picture.toString())
                putExtra("service_description", selectedService.aboutBusiness)
                putExtra("service_type", selectedService.services)
                putExtra("service_address", selectedService.address)
                putExtra("vendorId", selectedService.user_id.toString())
                putExtra("serviceId", selectedService.user_id)
                putExtra("service_price", selectedService.teamSize.toString())
            }
            Log.d("HomeFragment", "Navigating to BookingDetail with: ${selectedService.name}")
            startActivity(intent)
        }
        binding.rvHorizontalVendorList.adapter = allVendorsAdapter
    }

    private fun setupSlider(imageUrls: List<String>) {
        sliderAdapter = SliderAdapter(imageUrls)
        binding.imageService.adapter = sliderAdapter
        binding.dotsIndicator.setViewPager2(binding.imageService)
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
        viewModel1.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(requireContext())
            else CustomLoader.hideLoaderDialog()
        }

        viewModel1.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.modelService.observe(viewLifecycleOwner) { response ->
            response?.result?.let { services ->
                originalServiceList = services
                serviceAdapter.updateData(services)
                val imageUrls = services.map { it.image }
                setupSlider(imageUrls)
            } ?: run {
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.modelAllVendors.observe(viewLifecycleOwner) { response ->
            response?.result?.let { services ->
                allVendorsAdapter.updateData(services)
            } ?: run {
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel1.modelUserDetails.observe(viewLifecycleOwner) { response ->
            response?.result?.firstOrNull()?.let { userDetail ->
                sessionManager.username = userDetail.name
                sessionManager.profilePictureUrl = userDetail.profile_picture
                sessionManager.userType = userDetail.user_type
                getService()
            } ?: run {
                Toast.makeText(requireContext(), "No data available.", Toast.LENGTH_SHORT).show()
            }
        }

        categoryViewModel.modelCategory.observe(viewLifecycleOwner) { modelCategory ->
            originalCategoryList = modelCategory.result
            binding.rvCategory1.adapter =
                CategoryAdapter(originalCategoryList) { selectedCategory ->
                    userId = selectedCategory.id.toString()
                    val intent = Intent(requireContext(), ShowVendors::class.java).apply {
                        putExtra("category_id", selectedCategory.id.toString())
                        putExtra("category_name", selectedCategory.category_name)
                    }
                    startActivity(intent)
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
    fun getService(){
        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
                viewModel.getServiceList(token, sessionManager.userType.toString())
                viewModel.getAllVendors(token)
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Missing Token", Toast.LENGTH_LONG).show()
        }
    }
    @SuppressLint("SetTextI18n", "MissingPermission")
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient?.lastLocation
                ?.addOnSuccessListener { location ->
                    if (isAdded && location != null) {
                        try {
                            val context = context ?: return@addOnSuccessListener
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )

                            if (!addresses.isNullOrEmpty()) {
                                currentAddress = addresses[0].getAddressLine(0).toString()
                                binding.locationText.text = sessionManager.name
                                binding.subLocationText.text = addresses[0].locality
                            }
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

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                val filteredCategories = originalCategoryList.filter {
                    it.category_name.contains(query, ignoreCase = true)
                }
                (binding.rvCategory1.adapter as? CategoryAdapter)?.updateData(filteredCategories)

                val filteredServices = originalServiceList.filter {
                    it.serviceName.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true) ||
                            it.address.contains(query, ignoreCase = true)
                }
                serviceAdapter.updateData(filteredServices)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }

    companion object {
        var userId = ""
    }
}
