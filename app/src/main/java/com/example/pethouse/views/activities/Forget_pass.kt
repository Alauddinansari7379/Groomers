package com.example.pethouse.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pethouse.R
import com.example.pethouse.User.UserResister

class Forget_pass : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pass2)

         val emailField: EditText = findViewById(R.id.email)
        val oldPasswordField: EditText = findViewById(R.id.passwprd)
        val newPasswordField: EditText = findViewById(R.id.confirmPasswprd)

        // Change Password Button
        val changePasswordButton: Button = findViewById(R.id.facebook)
        changePasswordButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val oldPassword = oldPasswordField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()

            // Validate inputs
            if (email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulate password change (backend/database logic here)
            val isPasswordChanged = changePassword(email, oldPassword, newPassword)
            if (isPasswordChanged) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

//                // Navigate to Vendor Dashboard
//                val intent = Intent(this, Vender_dash::class.java)
//                startActivity(intent)
//                finish() // Close the Forget Password screen
            } else {
                Toast.makeText(this, "Failed to change password. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        // TextView for Register
        val register: TextView = findViewById(R.id.register)
        register.setOnClickListener {
            val intent = Intent(this, UserResister::class.java)
            startActivity(intent)
        }
    }

    // Simulate password change (replace this with actual backend/database logic)
    private fun changePassword(email: String, oldPassword: String, newPassword: String): Boolean {
        // Replace this with actual logic, e.g., API call or database query
        // For now, assume password change is always successful
        return true
    }
}
