package com.example.groomers.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.groomers.R
import com.example.groomers.activity.OrderDetails
import com.example.groomers.activity.OrderLists
import com.example.groomers.adapter.BookingsAdapter
import com.example.groomers.databinding.FragmentListUserBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.BookingListViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OrderListFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private val viewModel: BookingListViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager


    private var changeTextColor: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchBookings()
        observeViewModel()

        binding.mainLayout.setOnClickListener {
            startActivity(Intent(requireContext(), OrderDetails::class.java))
        }
    }


    private fun fetchBookings() {
        sessionManager.accessToken?.let { token ->
            viewModel.fetchBookingList(token)
        } ?: showError("Error: Missing Token")
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(requireContext())
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let { showError(it) }
        }

        viewModel.bookingList.observe(viewLifecycleOwner) { modelBookingList ->
            modelBookingList?.let { bookingData ->
                binding.rvBookings.apply {
                    adapter = BookingsAdapter(bookingData.result)
                }

            }
        }
    }

    private fun showError(message: String) {
        Toastic.toastic(
            context = requireContext(),
            message = message,
            duration = Toastic.LENGTH_SHORT,
            type = Toastic.ERROR,
            isIconAnimated = true,
            textColor = if (changeTextColor) Color.BLUE else null,
        ).show()
    }
}
