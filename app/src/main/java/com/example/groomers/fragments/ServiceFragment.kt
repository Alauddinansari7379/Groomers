package com.example.groomers.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.groomers.R
import com.example.groomers.activity.ViewOrderDetails
import com.example.groomers.adapter.Booking
import com.example.groomers.adapter.PopularServiceAdapter
import com.example.groomers.databinding.FragmentServiceBinding
import com.example.groomers.fragments.HomeFragment.Companion.userId
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.ServiceViewModel
import com.example.groomers.viewModel.SharedViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceFragment : Fragment(), Booking {
    lateinit var binding: FragmentServiceBinding
    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: ServiceViewModel by viewModels()
    private lateinit var serviceAdapter: PopularServiceAdapter
    private val shareViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentServiceBinding.bind(view)
        setupRecyclerView()
        observer()
    }
    fun observer(){
        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
//                viewModel.getServiceList(token, sessionManager.userType.toString())
//                viewModel.getServiceList(token, "Male")
//                shareViewModel.selectedItem.observe(viewLifecycleOwner) { event ->
//                    event.getContentIfNotHandled()?.let { item ->
                        viewModel.getServiceListByVendorId(token, userId.toInt())
//                    }
//                }


            }
        } ?: run {
            Toast.makeText(requireActivity(), "Error: Missing Token", Toast.LENGTH_LONG).show()
        }
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(requireActivity())
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(requireActivity()) { errorMessage ->
            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.modelAllPostByVendorId.observe(requireActivity()) { response ->
            response?.result?.let { services ->
                serviceAdapter.updateData(services) // Update adapter data instead of reinitializing
            } ?: run {
                Toast.makeText(requireActivity(), "No data available", Toast.LENGTH_SHORT).show()
            }
        }

//        viewModel.modelService.observe(requireActivity()) { response ->
//            response?.result?.let { services ->
//                serviceAdapter.updateData(services) // Update adapter data instead of reinitializing
//            } ?: run {
//                Toast.makeText(requireActivity(), "No data available", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
    private fun setupRecyclerView() {
        serviceAdapter = PopularServiceAdapter(emptyList(),requireActivity(),this)
        binding.rvPopularService.adapter = serviceAdapter
    }

    override fun booking(
        serviceName: String,
        description: String,
        image: String,
        price: Int,
        user_type: String,
        id: String,
        user_id: String,
        serviceType: String,
    ) {
        val intent = Intent(requireContext(), ViewOrderDetails::class.java).apply {
            putExtra("serviceName", serviceName)
            putExtra("description", description)
            putExtra("image", image)
            putExtra("price", price)
            putExtra("user_type", user_type)
            putExtra("serviceId", id)
            putExtra("vendorId", user_id)
            putExtra("categoryId", serviceType)
        }
        startActivity(intent)
    }
}