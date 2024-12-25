package com.example.pethouse.User

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityRegisterUserBinding
import com.example.pethouse.views.activities.Login

class UserResister : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    val context=this@UserResister
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnContinue.setOnClickListener {
                startActivity(Intent(context,AddMember::class.java))
            }


            tvLogin.setOnClickListener {
                startActivity(Intent(context,Login::class.java))
            }
        }
  }
}
