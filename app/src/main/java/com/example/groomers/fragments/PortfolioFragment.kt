package com.example.groomers.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail.Companion.aboutBusiness
import com.example.groomers.activity.BookingDetail.Companion.address
import com.example.groomers.activity.BookingDetail.Companion.serviceDescription
import com.example.groomers.activity.BookingDetail.Companion.serviceImage
import com.example.groomers.databinding.FragmentPortfolioBinding
import com.example.groomers.fragments.ServiceFragment.Companion.serviceNameList
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip

class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.stylistImage.load(R.drawable.women) {
//            crossfade(true)
//            transformations(CircleCropTransformation())
//        }
       // val services = listOf("Facial", "Pedicure", "Hair Treatment", "Haircut", "Threading", "Makeup", "Waxing", "Hair Styling", "Face Clean Up", "Hair Spa", "Hair Color", "Massage", "Manicure", "Body Scrub", "Beard Trim")
        addServiceTags(serviceNameList)
        binding.textName.text=serviceDescription
        binding.tvAbout.text=aboutBusiness
        binding.textBio.text=address
        serviceImage?.let {
            val baseUrl = "https://groomers.co.in/public/uploads/"
            val imageUrl = baseUrl + it
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.noimage)
                .into(binding.imageProfile)
        }

    }

    // UPDATED FUNCTION TO IMPROVE CHIP LAYOUT
    private fun addServiceTags(tags: List<String>) {
        val chipBackgroundColor = ContextCompat.getColor(requireContext(), R.color.chip_background)
        // Convert dp to pixels for margins
        val marginInPixels = (4 * resources.displayMetrics.density).toInt()

        for (tag in tags) {
            val chip = Chip(requireContext()).apply {
                text = tag
                setChipBackgroundColor(ColorStateList.valueOf(chipBackgroundColor))
                // Using a slightly darker text color for better contrast on the light blue
                setTextColor(Color.parseColor("#0D47A1"))
                isClickable = false
                isCheckable = false
            }

            // Create LayoutParams and set margins
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
            chip.layoutParams = params

            binding.servicesTagsLayout.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}