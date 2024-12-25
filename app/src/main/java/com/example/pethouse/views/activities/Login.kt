package com.example.pethouse.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.User.UserResister
 import com.example.pethouse.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    val context=this@Login
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            register.setOnClickListener {
                startActivity(Intent(context,UserResister::class.java))
            }

            btnLogin.setOnClickListener {
               // startActivity(Intent(context,Vender_dash::class.java))
            }
        }


    }
}
