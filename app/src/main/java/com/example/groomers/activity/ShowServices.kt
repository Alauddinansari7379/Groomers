package com.example.groomers.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.groomers.R
import com.example.groomers.adapter.VendorsListAdapter
import com.example.groomers.databinding.ActivityShowServicesBinding
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.LoginViewModel
import com.example.groomers.viewModel.ServiceViewModel
import com.example.groomers.viewModel.VendorListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShowServices : AppCompatActivity() {
    private val viewModel: VendorListViewModel by viewModels()
    private lateinit var adapter: VendorsListAdapter
    private val viewModel1: LoginViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager

    private val context = this@ShowServices
    private val binding by lazy { ActivityShowServicesBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
                viewModel1.getUserDetails()
                if (!sessionManager.userType.isNullOrEmpty()) {
                    viewModel.getServiceList(token, sessionManager.userType.toString())
                }
                viewModel.getAllVendors(token)
            }
        } ?: run {
            Toast.makeText(context, "Error: Missing Token", Toast.LENGTH_LONG).show()
        }

    }
}