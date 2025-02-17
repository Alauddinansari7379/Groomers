package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityLoginBinding
import com.example.groomers.viewModel.MyApplication

class Login : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }
    private val context=this@Login
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){

            btnContinue.setOnClickListener {
                val email = binding.etEmail.text.toString()
                viewModel.email = email
                if (email.isEmpty()) {
                    etEmail.error = "Please enter your email/username"
                    etEmail.requestFocus()
                    return@setOnClickListener
                }
                startActivity(Intent(context, Watching::class.java))
            }

            signInAsPro.setOnClickListener {
                startActivity(Intent(context,Contact::class.java))
            }
//
//            tvForget.setOnClickListener {
//                startActivity(Intent(context,ForgetPassword::class.java))
//            }
        }

    }
}
