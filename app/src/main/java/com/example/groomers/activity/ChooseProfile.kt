package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityChooseProfileBinding
import com.example.groomers.viewModel.MyApplication

class ChooseProfile : AppCompatActivity() {
    private val binding by lazy { ActivityChooseProfileBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.layoutMale.setOnClickListener {
            viewModel.profileType = "Male"
            startActivity(Intent(this@ChooseProfile, Registration::class.java))
        }
        binding.layoutFemale.setOnClickListener {
            viewModel.profileType = "Female"
            startActivity(Intent(this@ChooseProfile, Registration::class.java))
        }
        binding.layoutPet.setOnClickListener {
            viewModel.profileType = "Pet"
            startActivity(Intent(this@ChooseProfile, Registration::class.java))
        }

    }
}