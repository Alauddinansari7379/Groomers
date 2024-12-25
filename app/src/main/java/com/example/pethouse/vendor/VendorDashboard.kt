package com.example.pethouse.vendor
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.navigation.fragment.NavHostFragment
import com.example.pethouse.R
import com.example.pethouse.databinding.ActivityVenderDashBinding

class VendorDashboard : AppCompatActivity() {
private val binding by lazy { ActivityVenderDashBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostFragment) as? NavHostFragment
            ?: throw NullPointerException("NavHostFragment not found in layout")

        val navController = navHostFragment.navController
        val smoothBottomBar = binding.bottomNavigation1
            ?: throw NullPointerException("SmoothBottomBar not found in layout")

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_menu)
        smoothBottomBar.setupWithNavController(popupMenu.menu, navController)
        smoothBottomBar.onItemSelected = { position ->
            when (position) {
                0 -> loadFragment(HomeFragment())
                1 -> loadFragment(Add_post())
                2 -> loadFragment(ListFragment())
                3 -> loadFragment(Profile())
            }
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.hostFragment, fragment)
            .commit()
    }
}
