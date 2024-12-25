package com.example.pethouse.user
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.pethouse.R

class SubUserLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_user_login)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue))
        // Find the ImageView by its ID
        val imageView: ImageView = findViewById(R.id.image1)

        // Set a click listener on the ImageView
        imageView.setOnClickListener {
            // Navigate to the User Dashboard
            val intent = Intent(this, UserDashBoard::class.java)
            startActivity(intent)
        }
    }
}