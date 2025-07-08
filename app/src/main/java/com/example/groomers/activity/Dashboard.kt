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
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
@AndroidEntryPoint
class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityVenderDashBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private var destinationFrom: String = ""
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 100
    private var currentAddress = ""

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVenderDashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLastLocation()

        // ✅ Updated: using BottomNavigationView instead of CurvedBottomNavigation
        bottomNav = binding.bottomNavigation1

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigation()

        Glide.with(this)
            .load("https://groomers.co.in/public/uploads/" + sessionManager.profilePictureUrl)
            .placeholder(R.drawable.user)
            .into(binding.profileImage)

        destinationFrom = intent.getStringExtra("navigate_to") ?: ""
        if (destinationFrom == "fragment_cart") {
            navController.navigate(R.id.appointmentFragment)
            bottomNav.selectedItemId = ORDER_LIST
            destinationFrom = ""
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                HOME_ITEM -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                ORDER_LIST -> {
                    navController.navigate(R.id.appointmentFragment)
                    true
                }
                PROFILE -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = HOME_ITEM
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
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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
                                binding.tvAddress.text = area
                                binding.tvCity.text = city
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
