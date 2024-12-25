package com.example.pethouse.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.pethouse.R

class AboutYouActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_you)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue))

         val continebtn = findViewById<Button>(R.id.btnRegister)


        continebtn.setOnClickListener {
            val intent = Intent(this, BusinessSizeActivity::class.java)
            startActivity(intent)

        }
    }
}