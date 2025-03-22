package com.example.groomers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.groomers.R
import com.example.groomers.adapter.ViewPagerAdapter
import com.example.groomers.databinding.FragmentAppointmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class AppointmentFragment : Fragment() {
    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)

        // Setup ViewPager2 and TabLayout
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = createCustomTab(position)
        }.attach()

        return binding.root
    }

    private fun createCustomTab(position: Int): View {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        val tabText = view.findViewById<TextView>(R.id.tabText)

        when (position) {
            0 -> tabText.text = "Upcoming"
            1 -> tabText.text = "Confirmed"
            2 -> tabText.text = "Cancelled"
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
