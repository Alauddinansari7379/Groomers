package com.example.groomers.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groomers.R
import com.example.groomers.adapter.ProfileAdapter
import com.example.groomers.databinding.ActivityWatchingBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.viewModel.LoginViewModel
import com.example.groomers.viewModel.MultiuserListViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Watching : AppCompatActivity() {

    private val binding by lazy { ActivityWatchingBinding.inflate(layoutInflater) }
    private val viewModel: MultiuserListViewModel by viewModels()
    private val viewModel1: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.getAllVendorsByCategoryId()

        binding.recyclerViewProfiles.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        viewModel.multiuserList.observe(this) { response ->
            response?.result?.let { list ->
                val adapter = ProfileAdapter(list) { selectedProfile ->
                    showPasswordPromptDialog(selectedProfile.username, selectedProfile.email)
                }
                binding.recyclerViewProfiles.adapter = adapter
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toastic.toastic(
                    context = this@Watching,
                    message = it,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(this)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        binding.layoutAddPro.setOnClickListener {
            val intent = Intent(this, Contact::class.java)
            intent.putExtra("AddPro", "AddPro")
            startActivity(intent)
        }
        binding.skipText.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }
    }

    private fun showPasswordPromptDialog(username: String, email: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password_prompt, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val tvUsername = dialogView.findViewById<TextView>(R.id.tvUsername)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val btnContinue = dialogView.findViewById<Button>(R.id.btnContinue)
        val btnClose = dialogView.findViewById<ImageView>(R.id.ivClose)

        tvUsername.text = "Username: $username"

        btnContinue.setOnClickListener {
            val password = etPassword.text.toString()
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                callLoginApi(username, password)
            }
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun callLoginApi(username: String, password: String) {
        viewModel1.login("", username, password,"watch")
        // Observe isLoading to show/hide progress
        viewModel1.isLoading.observe(this@Watching,) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(this@Watching,)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        // Observe the result of the login attempt
        viewModel1.modelLogin.observe(this@Watching,) { modelLogin ->
            modelLogin?.let {
                // If login is successful, navigate to MainActivity
                Toastic.toastic(
                    context = this@Watching,
                    message = "Log in successful",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
                // Simulate success and go to Dashboard
                val intent = Intent(this, Dashboard::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllVendorsByCategoryId()
    }
}
