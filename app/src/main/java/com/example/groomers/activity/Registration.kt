package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.R
import com.example.groomers.databinding.ActivityRegisterUserBinding
import com.example.groomers.viewModel.MyApplication
import dagger.hilt.android.qualifiers.ApplicationContext

class Registration : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }
    val context = this@Registration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.e("ViewModelData", viewModel.profileType.toString())
        with(binding) {
            if (viewModel.profileType != "Pet") {
                tvNameTitle.text = getString(R.string.let_s_get_started_with_the_personal_profile)
                layoutGenderMain.visibility = View.GONE
                tvAge.text = getString(R.string.what_s_your_age)
                edtGender.setHint(getString(R.string.enter_gender))
                edtAge.setHint(getString(R.string.enter_age))
            }
            btnContinue.setOnClickListener {
                startActivity(Intent(context, Login::class.java))
            }

        }
    }
}
