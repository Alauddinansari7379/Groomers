package com.example.pethouse.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.databinding.ActivityAddMemberBinding

class AddMember : AppCompatActivity() {
    private val binding by lazy { ActivityAddMemberBinding.inflate(layoutInflater) }
    val context=this@AddMember
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnSignUp.setOnClickListener {
                startActivity(Intent(context,Dashboard::class.java))
            }

        }
  }
}
