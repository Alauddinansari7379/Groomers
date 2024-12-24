package com.example.pethouse.User

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pethouse.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserDashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue))

        val userbottomNav = findViewById<BottomNavigationView>(R.id.user_bottomNav)
        userbottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user_home -> {
                    loadFragment(Home_user())
                    true
                }
                R.id.user_list -> {
                    loadFragment(OrderList())
                    true
                }

                R.id.user_profile -> {
                    loadFragment(UserProfile())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(Home_user())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mtrl_calendar_frame, fragment)
            .commit()
    }
}