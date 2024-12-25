package com.example.pethouse.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.pethouse.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue))
        // Apply a blue color filter to the ImageView
        val splashScreen = findViewById<ImageView>(R.id.splash_screen)
        splashScreen.setColorFilter(Color.parseColor("#176EB3FF"), PorterDuff.Mode.SRC_IN)

        // Navigate to LoginActivity after a delay (e.g., 3 seconds)
        Handler().postDelayed({
            // Create an intent to navigate to LoginActivity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            // Optionally, finish MainActivity to prevent going back to it
            finish()
        }, 6000)  // 6000 milliseconds = 6 seconds
    }
}
