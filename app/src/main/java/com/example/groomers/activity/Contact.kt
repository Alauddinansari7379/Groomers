package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityContactBinding
import com.example.groomers.viewModel.MyApplication
import java.util.regex.Pattern

class Contact : AppCompatActivity() {
    val binding by lazy { ActivityContactBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            if (intent.getStringExtra("AddPro").toString() == "AddPro") {
                layoutEmail.visibility = View.GONE
            }
            val countryCodeWithPlus: String =
                spinnerCountryCode.selectedCountryCodeWithPlus // Example: "+91"

            btnContinue.setOnClickListener {

                viewModel.email = edtEmail.text.toString().trim()
                viewModel.mobile = countryCodeWithPlus + edtPhone.text.toString().trim()
                viewModel.password = edtPassword.text.toString().trim()
                viewModel.password_confirmation = edtPassword.text.toString().trim()
                if (viewModel.email!!.isEmpty()) {
                    edtEmail.error = "Please enter your email"
                    edtEmail.requestFocus()
                    return@setOnClickListener
                }
                if (!isValidEmail(edtEmail.text.toString().trim())) {
                    edtEmail.error = "Please enter a valid email"
                    edtEmail.requestFocus()
                    return@setOnClickListener
                }
                if (viewModel.password!!.isEmpty()) {
                    edtPassword.error = "Please enter your password"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }
                if (viewModel.password!!.length < 6) {
                    edtPassword.error = "Please enter at least 6 digit password"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }
                if (viewModel.mobile!!.isEmpty()) {
                    edtPhone.error = "Please enter your phone number"
                    edtPhone.requestFocus()
                    return@setOnClickListener
                }

                if (viewModel.mobile!!.length < 10) {
                    edtPhone.error = "Please enter a valid phone number"
                    edtPhone.requestFocus()
                    return@setOnClickListener
                }
                startActivity(
                    Intent(
                        this@Contact,
                        ChooseProfile::class.java
                    )
                )
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }


}