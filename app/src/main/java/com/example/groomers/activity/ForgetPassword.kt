package com.example.groomers.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ehcf.Helper.myToast
import com.example.groomers.databinding.ActivityForgetPasswordBinding
import com.example.groomers.model.modelForgot.ModelForgot
import com.example.groomers.retrofit.ApiClient
import com.example.groomers.retrofit.ApiService
import com.example.groomers.sharedpreferences.SessionManager
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPassword : AppCompatActivity() {
    private val context = this@ForgetPassword

    @Inject
    lateinit var apiService: ApiService
    var count = 0
    var otp = ""

    @Inject
    lateinit var sessionManager: SessionManager
    private val binding by lazy { ActivityForgetPasswordBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            btnSendOtp.setOnClickListener {
                if (binding.etEmail.text.toString().isEmpty()) {
                    myToast(context, "Enter email", false)
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }
                apiCallSendOTP()
            }


            tvRegister.setOnClickListener {
                startActivity(Intent(context, Contact::class.java))
            }
        }
    }

    private fun apiCallSendOTP() {
        CustomLoader.showLoaderDialog(context)
        ApiClient.apiService.forgotPassword(binding.etEmail.text.toString().trim(), "user")
            .enqueue(object : Callback<ModelForgot> {
                override fun onResponse(
                    call: Call<ModelForgot>, response: Response<ModelForgot>
                ) {
                    try {
                        CustomLoader.hideLoaderDialog()
                        when (response.code()) {
                            404 -> myToast(context, "Something went wrong", false)
                            500 -> myToast(context, "Server Error", false)
                            else -> {
                                if (response.body()!!.status==1){
                                    myToast(context, "OTP sent successfully", true)
                                    otp = response.body()?.result!!.otp.toString()
                                    val intent = Intent(context, EnterOTP::class.java)
                                    intent.putExtra("OTP", otp)
                                    intent.putExtra("Email", binding.etEmail.text.toString().trim())
                                    context.startActivity(intent)
                                }else{
                                    myToast(context, response.body()!!.message, false)

                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong", false)
                        CustomLoader.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelForgot>, t: Throwable) {
                    CustomLoader.hideLoaderDialog()
                    count++
                    if (count <= 3) {
                        Log.e("count", count.toString())
                        apiCallSendOTP()
                    } else {
                        myToast(context, t.message.toString(), false)
                        CustomLoader.hideLoaderDialog()
                    }
                }
            })
    }

}