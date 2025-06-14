package com.example.groomers.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.groomers.R
import com.example.groomers.adapter.BookingsAdapterCanclled
import com.example.groomers.adapter.BookingsAdapterConfirm
import com.example.groomers.databinding.FragmentCancelledBinding
import com.example.groomers.databinding.FragmentConfirmedBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.BookingListViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CancelledFragment : Fragment() {
    lateinit var binding: FragmentCancelledBinding

    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: BookingListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancelled, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCancelledBinding.bind(view)
        fetchBookings()
        observeViewModel()

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
                    adapter = BookingsAdapterCanclled(bookingData.result, requireContext())

                }
                for (i in bookingData.result){
                    if (i.slug!="rejected"){
                        binding.tvNoDataFound.visibility=View.GONE
                        break
                    }else{
                        binding.tvNoDataFound.visibility = View.VISIBLE

                    }
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
            textColor = if (false) Color.BLUE else null,
        ).show()
    }
}