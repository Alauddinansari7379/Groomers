package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityAddMemberBinding

class AddMember : AppCompatActivity() {
    private val binding by lazy { ActivityAddMemberBinding.inflate(layoutInflater) }
    val context=this@AddMember
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val items = arrayOf("Sub Member 1", "Sub Member 2", "Sub Member 3", "Sub Member 4")


        with(binding){
            btnSignUp.setOnClickListener {
                startActivity(Intent(context,Dashboard::class.java))
            }
            btnAdd.setOnClickListener {
                val builder = AlertDialog.Builder(this@AddMember)
                builder.setTitle("Select an Sub Member")
                builder.setItems(items) { _, which ->
                    etMember.setText(items[which])
                }
                builder.create().show()
            }
        }
  }
}
