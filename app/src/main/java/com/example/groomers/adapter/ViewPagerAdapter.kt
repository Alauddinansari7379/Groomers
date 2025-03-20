package com.example.groomers.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.groomers.fragments.CancelledFragment
import com.example.groomers.fragments.ConfirmedFragment
import com.example.groomers.fragments.UpcomingFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(UpcomingFragment(), ConfirmedFragment(), CancelledFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}