package com.example.groomers.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.groomers.fragments.DetailsFragment
import com.example.groomers.fragments.PortfolioFragment
import com.example.groomers.fragments.ReviewsFragment
import com.example.groomers.fragments.ServiceFragment

class ViewPagerAdapter1(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(
        ServiceFragment(),
        ReviewsFragment(),
        PortfolioFragment(),
        DetailsFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
