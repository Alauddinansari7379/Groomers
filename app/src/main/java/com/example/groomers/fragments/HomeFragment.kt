package com.example.groomers.fragments
import HomeAdapter
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
import android.widget.HorizontalScrollView
import android.widget.ImageView
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail
import com.example.groomers.adapter.ServiceAdapter
import com.example.groomers.databinding.FragmentHomeUserBinding
import com.example.groomers.databinding.FragmentListUserBinding
import com.example.groomers.model.Item
import com.example.groomers.sharedpreferences.SessionManager
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
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentAddress = ""
    private val REQUEST_CODE = 100
    private lateinit var currentLocation: TextView
    private lateinit var horizontalScroll: CardView
    private lateinit var serviceAdapter: ServiceAdapter
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
        currentLocation = view.findViewById<TextView>(R.id.tvLocation)
//        horizontalScroll = view.findViewById<CardView>(R.id.imageItem1)
//        horizontalScroll.setOnClickListener {
//            val intent = Intent(requireContext(), BookingDetail::class.java)
//            startActivity(intent)
//        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
//        getLastLocation()

        setupRecyclerView()
        observeViewModel()

        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
                viewModel.getServiceList("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2dyb29tZXJzLmNvLmluL2FwaS9sb2dpbiIsImlhdCI6MTczOTM1Njg2NiwiZXhwIjoxNzQwNjUyODY2LCJuYmYiOjE3MzkzNTY4NjYsImp0aSI6IllsbmRjSkx1YTZubnBaTm0iLCJzdWIiOiIzMSIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.l4IwWgQpJGORNXe1BbDA2o3rIhVmRDlw4V9Adg10QMU")
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Missing Token", Toast.LENGTH_LONG).show()
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
                    if (location != null && isAdded) {
                        try {
                            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            Log.e(
                                ContentValues.TAG,
                                "addresses[0].latitude: ${addresses?.get(0)?.latitude}"
                            )
                            Log.e(
                                ContentValues.TAG,
                                "addresses[0].longitude: ${addresses?.get(0)?.longitude}"
                            )

                            addresses?.get(0)?.getAddressLine(0)

                            val locality = addresses?.get(0)?.locality
                            val countryName = addresses?.get(0)?.countryName
                            val countryCode = addresses?.get(0)?.countryCode
                            val postalCode = addresses?.get(0)?.postalCode.toString()
                            val subLocality = addresses?.get(0)?.subLocality
                            val subAdminArea = addresses?.get(0)?.subAdminArea
                            currentAddress = "$subLocality, $locality, $countryName"
                            postalCodeNew = postalCode

                            // Update location text
                            currentLocation.text = addresses?.get(0)?.getAddressLine(0)

                            Log.e(ContentValues.TAG, "locality: $locality")
                            Log.e(ContentValues.TAG, "countryName: $countryName")
                            Log.e(ContentValues.TAG, "countryCode: $countryCode")
                            Log.e(ContentValues.TAG, "postalCode: $postalCode")
                            Log.e(ContentValues.TAG, "subLocality: $subLocality")
                            Log.e(ContentValues.TAG, "subAdminArea: $subAdminArea")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
        } else {
            askPermission()
        }
    }


    companion object {
        var postalCodeNew = ""
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(emptyList()) { selectedService ->
            val intent = Intent(requireContext(), BookingDetail::class.java).apply {
                putExtra("service_name", selectedService.serviceName)
                putExtra("service_image", selectedService.image)
                putExtra("service_description", selectedService.description)
                putExtra("service_type", selectedService.serviceType)
                putExtra("service_address", selectedService.address)
            }
            Log.d("HomeFragment", "Navigating to BookingDetail with: ${selectedService.serviceName}")
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

    }

    }

