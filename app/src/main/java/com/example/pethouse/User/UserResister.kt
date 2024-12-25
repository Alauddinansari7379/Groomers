package com.example.pethouse.User

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.views.activities.AboutYouActivity
import com.example.pethouse.R
import com.example.pethouse.Vender.RegisterVender
import com.example.pethouse.adapter.TextGridAdapter
import com.example.pethouse.databinding.ActivityRegisterUserBinding
import com.example.pethouse.databinding.ActivityUserDashBinding
import com.example.pethouse.views.activities.Login
import com.google.android.material.textfield.TextInputLayout

class UserResister : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    val context=this@UserResister
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnContinue.setOnClickListener {
                startActivity(Intent(context,AddMember::class.java))
            }

            tvLogin.setOnClickListener {
                startActivity(Intent(context,Login::class.java))
            }
        }
  }
}
