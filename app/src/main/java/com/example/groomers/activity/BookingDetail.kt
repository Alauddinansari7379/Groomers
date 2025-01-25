package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityBookingDetailBinding

class BookingDetail : AppCompatActivity() {
    private val binding by lazy { ActivityBookingDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.bookButton111.setOnClickListener {
            val intent = Intent(this@BookingDetail, Dashboard::class.java)
            intent.putExtra("navigate_to", "fragment_cart")
            startActivity(intent)
        }

    }
}