package com.example.groomers.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.groomers.R
import com.example.groomers.activity.ViewOrderDetails
import com.example.groomers.adapter.Booking
import com.example.groomers.adapter.PopularServiceAdapter
import com.example.groomers.databinding.FragmentServiceBinding
import com.example.groomers.fragments.HomeFragment.Companion.userId
import com.example.groomers.model.modelservice.Result
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.ServiceViewModel
import com.example.groomers.viewModel.SharedViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceFragment : Fragment(), Booking {

    private lateinit var binding: FragmentServiceBinding

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: ServiceViewModel by viewModels()
    private val shareViewModel: SharedViewModel by activityViewModels()

    private lateinit var serviceAdapter: PopularServiceAdapter
    private var fullServiceList: List<Result> = emptyList() // For search filtering

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentServiceBinding.bind(view)

        setupRecyclerView()
        setupSearch()


        binding.edtSearch.addTextChangedListener { str ->
            if (fullServiceList.isNotEmpty()) {
                val filtered = fullServiceList.filter {
                    it.serviceName?.contains(str.toString(), ignoreCase = true) == true ||
                            it.description?.contains(str.toString(), ignoreCase = true) == true
                }
                serviceAdapter.updateData(filtered)
            }
        }
        observeViewModel()
    }

    private fun setupRecyclerView() {
        serviceAdapter = PopularServiceAdapter(emptyList(), requireActivity(), this)
        binding.rvPopularService.adapter = serviceAdapter
    }

    private fun setupSearch() {

    }

    private fun observeViewModel() {
        sessionManager.accessToken?.let { token ->
            lifecycleScope.launch {
                viewModel.getServiceListByVendorId(token, userId.toInt())
            }
        } ?: run {
            Toast.makeText(requireActivity(), "Missing token", Toast.LENGTH_LONG).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(requireActivity())
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.modelAllPostByVendorId.observe(viewLifecycleOwner) { response ->
            response?.result?.let { services ->
                fullServiceList = services
                serviceAdapter.updateData(services)
            } ?: run {
                Toast.makeText(requireActivity(), "No data available", Toast.LENGTH_SHORT).show()
            }
        }
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
        address: String,
        time: String
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
            putExtra("address", address)
            putExtra("time", time)
        }
        startActivity(intent)
    }
}
