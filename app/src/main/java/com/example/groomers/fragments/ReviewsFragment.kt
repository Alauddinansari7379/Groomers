package com.example.groomers.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.groomers.activity.BookingDetail.Companion.compVendorId
import com.example.groomers.adapter.ReviewAdapter
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
                    viewModel.getAllVendorRatings(compVendorId)
                }


        // Observe isLoading to show/hide progress
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                activity?.let { CustomLoader.showLoaderDialog(it) }
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        viewModel.modelVendorRating.observe(requireActivity()) { modelRating ->
            if (modelRating?.status == 1) {
                val overAllRating = calculateOverallRating(modelRating.result).toString()
                val counts = getStarRatingCounts(modelRating.result)
                updateRatingProgressBars(counts, modelRating.result.size)
                binding.ratingBarAverage.text = overAllRating
                binding.ratingBarDProfile.rating = overAllRating.toFloat()
                binding.rvReviewList.apply {
                    adapter = ReviewAdapter(modelRating.result,requireActivity())
                }

            }
        }

        // Observe error message if login fails
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                activity?.let {
                    Toastic.toastic(
                        context = it,
                        message = errorMessage,
                        duration = Toastic.LENGTH_SHORT,
                        type = Toastic.ERROR,
                        isIconAnimated = true,
                        textColor = if (false) Color.BLUE else null,
                    ).show()
                }
            }
        }



    }
    private fun calculateOverallRating(reviews: List<Result>): Float {
        if (reviews.isEmpty()) return 0f
        val totalRating = reviews.sumOf { it.rating }
        return totalRating.toFloat() / reviews.size
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun getStarRatingCounts(reviews: List<Result>): Map<Int, Int> {
        val starCounts = mutableMapOf(
            1 to 0,
            2 to 0,
            3 to 0,
            4 to 0,
            5 to 0
        )

        for (review in reviews) {
            val rating = review.rating.toInt()
            if (rating in 1..5) {
                starCounts[rating] = starCounts[rating]!! + 1
            }
        }

        return starCounts
    }
    private fun updateRatingProgressBars(
        counts: Map<Int, Int>,
        totalReviews: Int
    ) {
        val progressBars = mapOf(
            5 to binding.pbStar5,
            4 to binding.pbStar4,
            3 to binding.pbStar3,
            2 to binding.pbStar2,
            1 to binding.pbStar1
        )

        val countLabels = mapOf(
            5 to binding.tvStarCount5,
            4 to binding.tvStarCount4,
            3 to binding.tvStarCount3,
            2 to binding.tvStarCount2,
            1 to binding.tvStarCount1
        )

        for (star in 1..5) {
            val count = counts[star] ?: 0
            val percent = if (totalReviews > 0) (count * 100 / totalReviews) else 0

            progressBars[star]?.progress = percent
            countLabels[star]?.text = count.toString()
        }
    }

}
