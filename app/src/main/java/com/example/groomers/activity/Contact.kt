package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityContactBinding
import com.example.groomers.viewModel.MyApplication

class Contact : AppCompatActivity() {
    val binding by lazy { ActivityContactBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val email = viewModel.email ?: ""
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            if (intent.getStringExtra("AddPro").toString()=="AddPro"){
                layoutEmail.visibility=View.GONE
            }
            btnContinue.setOnClickListener {
                startActivity(
                    Intent(
                        this@Contact,
                        ChooseProfile::class.java
                    )
                )
            }
        }
    }
}