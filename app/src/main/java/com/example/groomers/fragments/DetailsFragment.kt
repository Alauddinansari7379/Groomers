package com.example.groomers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail.Companion.serviceImage
import com.example.groomers.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment()  {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.salonInteriorImage.load(R.drawable.b3) {
//            crossfade(true)
//        }
//        binding.btBookSlot.setOnClickListener{
//            startActivity(Intent(requireContext(),ViewOrderDetails::class.java))
//        }
        serviceImage?.let {
            val baseUrl = "https://groomers.co.in/public/uploads/"
            val imageUrl = baseUrl + it
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.noimage)
                .into(binding.salonInteriorImage)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}