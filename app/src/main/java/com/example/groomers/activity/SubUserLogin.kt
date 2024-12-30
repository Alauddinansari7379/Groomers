package com.example.groomers.activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityLoginBinding
import com.example.groomers.databinding.ActivitySubUserLoginBinding

class SubUserLogin : AppCompatActivity() {
    private val binding by lazy { ActivitySubUserLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            subUser1.setOnClickListener { startActivity(Intent(this@SubUserLogin, Dashboard::class.java))}
            subUser2.setOnClickListener { startActivity(Intent(this@SubUserLogin, Dashboard::class.java))}
            subUser3.setOnClickListener { startActivity(Intent(this@SubUserLogin, Dashboard::class.java))}
            subUser4.setOnClickListener { startActivity(Intent(this@SubUserLogin, Dashboard::class.java))}

        }
    }
}