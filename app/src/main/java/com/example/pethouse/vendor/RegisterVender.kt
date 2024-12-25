package com.example.pethouse.vendor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.R
import com.example.pethouse.user.UserResister
import com.google.android.material.textfield.TextInputLayout

class RegisterVender : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_register_vender)


       val addbutton = findViewById<TextInputLayout>(R.id.tipSubUser)
        val User = findViewById<Switch>(R.id.checkBox)
        val Vender = findViewById<Button>(R.id.btnRegister)


//        addbutton.setOnClickListener {
//            val intent = Intent(this, AboutYouActivity::class.java)
//            startActivity(intent)
//
//        }

        User.setOnClickListener {

            val intent = Intent(this, UserResister::class.java)
            startActivity(intent)
        }
        Vender.setOnClickListener {

            val intent = Intent(this, VendorDashboard::class.java)
            startActivity(intent)
        }
    }
}