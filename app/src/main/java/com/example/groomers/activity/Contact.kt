package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
            val countryCodeWithPlus: String = spinnerCountryCode.selectedCountryCodeWithPlus // Example: "+91"

            btnContinue.setOnClickListener {
                viewModel.email=edtEmail.text.toString().trim()
                viewModel.mobile=countryCodeWithPlus+edtPhone.text.toString().trim()
                viewModel.password=edtPassword.text.toString().trim()
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