package com.example.groomers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groomers.adapter.ReviewAdapter
import com.example.groomers.databinding.FragmentReviewsBinding
import com.example.groomers.model.Review

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val reviews = listOf(
//            Review("Alice", 5f, "Fantastic app! Really helped me."),
//            Review("Bob", 4f, "Pretty good, but needs improvement."),
//            Review("Charlie", 3.5f, "It's okay. Could use more features."),
//            Review("Dana", 4.5f, "Clean interface and useful."),
//            Review("Eli", 5f, "Love it! Highly recommended.")
//        )
//
//        adapter = ReviewAdapter(reviews)
//        binding.rvReviewList.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvReviewList.adapter = adapter
//
//        val average = reviews.map { it.rating }.average().toFloat()
//        binding.ratingBarAverage.rating = average
//        binding.tvAverageScore.text = "${"%.1f".format(average)} / 5"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
