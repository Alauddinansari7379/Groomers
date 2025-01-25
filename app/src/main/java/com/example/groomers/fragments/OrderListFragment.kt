package com.example.groomers.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.groomers.R
import com.example.groomers.activity.OrderDetails
import com.example.groomers.activity.OrderLists


class OrderListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_user, container, false)
        view.findViewById<LinearLayout>(R.id.mainLayout).setOnClickListener {
            val intent = Intent(requireContext(), OrderDetails::class.java)
            startActivity(intent)
        }

        return view
    }

}