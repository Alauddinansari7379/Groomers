package com.example.groomers.activity

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.groomers.R
import com.example.groomers.databinding.ActivityVenderDashBinding
import me.ibrahimsn.lib.SmoothBottomBar

class Dashboard : AppCompatActivity() {
    private val binding by lazy { ActivityVenderDashBinding.inflate(layoutInflater) }
    lateinit var bottomNav: SmoothBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bottomNav = binding.bottomNavigation1

        // Get NavHostFragment and NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Set up SmoothBottomBar with NavController
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_menu)
        binding.bottomNavigation1.setupWithNavController(popupMenu.menu, navController)

        // Change the title based on the current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.tvTitle.text = when (destination.id) {
                R.id.homeFragment -> "Dashboard"
                R.id.addPostFragment -> "Post"
                R.id.orderListFragment -> "Order List"
                else -> "Profile"
            }
        }
    }
}
