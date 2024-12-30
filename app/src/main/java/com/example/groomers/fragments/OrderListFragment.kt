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
        view.findViewById<Button>(R.id.btn_930am).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        view . findViewById < Button >(R.id.btn_930am).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        view . findViewById < Button >(R.id.btn_1030am).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        view . findViewById < Button >(R.id.btn_120pm).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        view . findViewById < Button >(R.id.btn_510pm).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        view . findViewById < Button >(R.id.btn_9pm).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        view . findViewById < Button >(R.id.btn_10pm).setOnClickListener {
            val intent = Intent(requireContext(), OrderLists::class.java)
            startActivity(intent)
        }
        return view
    }

}