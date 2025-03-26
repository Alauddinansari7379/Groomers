package com.example.groomers.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.groomers.R
import com.example.groomers.databinding.ActivityVenderDashBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityVenderDashBinding
    private lateinit var bottomNav: CurvedBottomNavigation
    private lateinit var navController: NavController
    private var destinationFrom: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the view correctly
        binding = ActivityVenderDashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the bottom navigation
        bottomNav = binding.bottomNavigation1

        // Set up the NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize bottom navigation correctly
        setupBottomNavigation()

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

}
