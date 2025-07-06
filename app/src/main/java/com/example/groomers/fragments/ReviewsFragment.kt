package com.example.groomers.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.groomers.databinding.FragmentReviewsBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.model.modelvendorrating.Result
import com.example.groomers.viewModel.RatingViewModel
import com.example.groomers.viewModel.SharedViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RatingViewModel by viewModels()
    private val shareViewModel: SharedViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                lifecycleScope.launch {
//                    viewModel.getAllVendorRatings(compVendorId)
                    viewModel.getAllVendorRatings("13")
                }


        // Observe isLoading to show/hide progress
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(requireActivity())
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        viewModel.modelVendorRating.observe(requireActivity()) { modelRating ->
            if (modelRating?.status == 1) {
                var overAllRating = calculateOverallRating(modelRating.result).toString()
                binding.tvAverageRating.text = overAllRating
                binding.ratingBarAverage.rating = overAllRating.toFloat()

            }
        }

        // Observe error message if login fails
        viewModel.errorMessage.observe(requireActivity()) { errorMessage ->
            if (errorMessage!!.isNotEmpty()) {
                Toastic.toastic(
                    context = requireActivity(),
                    message = errorMessage,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }


    }
    fun calculateOverallRating(reviews: List<Result>): Float {
        if (reviews.isEmpty()) return 0f
        val totalRating = reviews.sumOf { it.rating }
        return totalRating.toFloat() / reviews.size
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
