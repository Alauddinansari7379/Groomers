package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityOrderListsBinding

class OrderLists : AppCompatActivity() {
    private val binding by lazy { ActivityOrderListsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.llmain.setOnClickListener {
            startActivity(Intent(this@OrderLists,OrderDetails::class.java))
        }

    }
}