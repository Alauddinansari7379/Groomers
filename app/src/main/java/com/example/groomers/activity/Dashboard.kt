package com.example.groomers.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.databinding.ActivityVenderDashBinding
import com.example.groomers.sharedpreferences.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityVenderDashBinding
    private lateinit var bottomNav: CurvedBottomNavigation
    private lateinit var navController: NavController
    private var destinationFrom: String = ""
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 100
    private var currentAddress = ""
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the view correctly
        binding = ActivityVenderDashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLastLocation()
        // Initialize the bottom navigation
        bottomNav = binding.bottomNavigation1

        // Set up the NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize bottom navigation correctly
        setupBottomNavigation()
        Glide.with(this)
            .load("https://groomers.co.in/public/uploads/" + sessionManager.profilePictureUrl)
            .placeholder(R.drawable.user) // Default placeholder
            .into(binding.profileImage)

        // Handle navigation based on the intent
        destinationFrom = intent.getStringExtra("navigate_to") ?: ""
        if (destinationFrom == "fragment_cart") {
            navController.navigate(R.id.appointmentFragment)
            bottomNav.show(ORDER_LIST) // ✅ Corrected to show the selected item
            destinationFrom = "" // Reset destinationFrom after navigation
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationItems = listOf(
            CurvedBottomNavigation.Model(
                HOME_ITEM, getString(R.string.home), R.drawable.baseline_home_24
            ),
            CurvedBottomNavigation.Model(
                ORDER_LIST, getString(R.string.appointment), R.drawable.baseline_calendar_today_24
            ),
            CurvedBottomNavigation.Model(
                PROFILE, getString(R.string.profile), R.drawable.baseline_person_24
            ),
        )

        bottomNav.apply {
            bottomNavigationItems.forEach { add(it) }
            setOnClickMenuListener {
                when (it.id) {
                    HOME_ITEM -> navController.navigate(R.id.homeFragment)
                    ORDER_LIST -> navController.navigate(R.id.appointmentFragment)
                    PROFILE -> navController.navigate(R.id.profileFragment)
                }
            }
            show(HOME_ITEM)
        }
    }


    companion object {
        val HOME_ITEM = R.id.homeFragment
        val ORDER_LIST = R.id.appointmentFragment
        val PROFILE = R.id.profileFragment
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient = com.google.android.gms.location.LocationServices
                .getFusedLocationProviderClient(this)

            fusedLocationProviderClient?.lastLocation
                ?.addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(this@Dashboard, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )

                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val area = address.subLocality ?: "Unknown Area"
                                val city = address.locality ?: "Unknown City"

                                binding.tvAddress.text = area   // e.g. Andheri East
                                binding.tvCity.text = city      // e.g. Mumbai
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
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }


}
