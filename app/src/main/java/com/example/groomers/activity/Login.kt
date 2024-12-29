package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val context=this@Login
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnLogin.setOnClickListener {
                startActivity(Intent(context,Dashboard::class.java))
            }

            tvRegister.setOnClickListener {
                startActivity(Intent(context,Registration::class.java))
            }

            tvForget.setOnClickListener {
                startActivity(Intent(context,ForgetPassword::class.java))
            }
        }

    }
}
