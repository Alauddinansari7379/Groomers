package com.example.groomers.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.groomers.R
import com.example.groomers.activity.Rating
import com.example.groomers.adapter.BookingsAdapter
import com.example.groomers.databinding.FragmentUpcomingBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.BookingListViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpcomingFragment : Fragment(),BookingsAdapter.Review {
    lateinit var binding: FragmentUpcomingBinding
    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: BookingListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUpcomingBinding.bind(view)
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
                    adapter = BookingsAdapter(bookingData.result,requireContext(),this@UpcomingFragment)
                }

                val allWaiting = bookingData.result.all { it.slug == "waiting_for_accept" }

                if (!allWaiting) {
                    binding.tvNoDataFound.visibility = View.VISIBLE
                } else {
                    binding.tvNoDataFound.visibility = View.GONE
                }


            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchBookings()
        observeViewModel()
    }

    override fun onPause() {
        super.onPause()
        fetchBookings()
        observeViewModel()
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

    override fun rating(bookingId: String) {
        val intent = Intent(requireActivity(), Rating::class.java)
            .putExtra("meetingId", bookingId)
        startActivity(intent)

    }


}