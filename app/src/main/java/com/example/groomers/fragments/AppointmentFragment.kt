package com.example.groomers.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.groomers.R
import com.example.groomers.activity.BookingDetail
import com.example.groomers.activity.ReviewAndConfirm

class AppointmentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)
        val btnContinue =view.findViewById<Button>(R.id.continueButton)
        btnContinue.setOnClickListener {
            val intent = Intent(requireContext(), ReviewAndConfirm::class.java)
            startActivity(intent)
        }
        return view
    }

}