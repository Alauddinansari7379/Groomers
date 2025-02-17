package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityWatchingBinding

class Watching : AppCompatActivity() {
    private val binding by lazy { ActivityWatchingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            profilesGrid.setOnClickListener {
                startActivity(
                    Intent(
                        this@Watching,
                        Dashboard::class.java
                    )
                )
            }

            layoutAddPro.setOnClickListener {
                val intent = Intent(this@Watching, Contact::class.java).apply {
                    putExtra("AddPro", "AddPro")
                }
                startActivity(intent)
            }


        }


    }
}