package com.example.pethouse.Vender
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pethouse.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Vender_dash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vender_dash)

         val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ome -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.list -> {
                    loadFragment(list())
                    true
                }
                R.id.post -> {
                    loadFragment(Add_post())
                    true
                }
                R.id.profile -> {
                    loadFragment(Profile())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mtrl_calendar_frame, fragment)
            .commit()
    }
}
