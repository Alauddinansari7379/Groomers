package com.example.pethouse.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.pethouse.R
import com.example.pethouse.vendor.VendorDashboard

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue))
        // Initialize views
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<AppCompatButton>(R.id.btnLogin)
        val registerText = findViewById<TextView>(R.id.register)
        val forgotPasswordText = findViewById<TextView>(R.id.forget)



        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

           // if (email.isEmpty() || password.isEmpty()) {
             //   Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
          //  } else {
                // Simulated login logic for any user
              //  if (isValidEmail(email)) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Navigate to Vendor Dashboard
                    val intent = Intent(this, VendorDashboard::class.java)
                    startActivity(intent)
                    finish() // Close the login activity
               // } else {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
              //  }
            }
       // }

        // Navigate to register activity
//        registerText.setOnClickListener {
//            val intent = Intent(this, UserResister::class.java)
//            startActivity(intent)
//        }

        // Navigate to forgot password activity
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, Forget_pass::class.java)
            startActivity(intent)
        }
    }

    // Helper method to validate email format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
