package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityChooseProfileBinding

class ChooseProfile : AppCompatActivity() {
    private val binding by lazy { ActivityChooseProfileBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.root.setOnClickListener { startActivity(Intent(this@ChooseProfile,Registration::class.java)) }

    }
}