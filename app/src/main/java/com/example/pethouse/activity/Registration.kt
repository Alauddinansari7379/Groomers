package com.example.pethouse.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityRegisterUserBinding

class Registration : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    val context=this@Registration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnContinue.setOnClickListener {
                startActivity(Intent(context, AddMember::class.java))
            }
            tvLogin.setOnClickListener {
                startActivity(Intent(context, Login::class.java))
            }
        }
  }
}
