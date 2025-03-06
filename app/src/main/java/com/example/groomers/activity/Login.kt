package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.databinding.ActivityLoginBinding
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.LoginViewModel
import com.example.groomers.viewModel.MyApplication
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Login : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val context = this@Login

    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (sessionManager.isLogin) {
            startActivity(Intent(this@Login, Dashboard::class.java))
            finish()
        }
        with(binding) {

            btnContinue.setOnClickListener {
                var email = ""
                var username = ""
                val password = binding.edtPassword.text.toString()


                val editText = binding.etEmail.text.toString()
                if (editText.isNotEmpty()) {
                    val input = editText.substringAfterLast("@")

                    if (input.equals("gmail.com", ignoreCase = true)) {
                        email = binding.etEmail.text.toString()
                    } else {
                        username = binding.etEmail.text.toString()
                    }
                }else{
                    binding.etEmail.error = "Please enter your email or username"
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    binding.edtPassword.error = "Please enter your password"
                    binding.edtPassword.requestFocus()
                    return@setOnClickListener
                }

                // If both fields are non-empty, proceed with login
                viewModel.login(email, username, password,)

            }

            signInAsPro.setOnClickListener {
                startActivity(Intent(context, Contact::class.java))
            }
//
//            tvForget.setOnClickListener {
//                startActivity(Intent(context,ForgetPassword::class.java))
//            }
        }


        // Observe isLoading to show/hide progress
        viewModel.isLoading.observe(context) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(context)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        // Observe the result of the login attempt
        viewModel.modelLogin.observe(context) { modelLogin ->
            modelLogin?.let {
                // If login is successful, navigate to MainActivity
                startActivity(Intent(context, Watching::class.java))
                finish()
            }
        }

        // Observe error message if login fails
        viewModel.errorMessage.observe(context) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }
}
