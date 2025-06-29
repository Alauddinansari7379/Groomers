package com.example.groomers.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.groomers.databinding.ActivityContactBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.viewModel.MultiuserListViewModel
import com.example.groomers.viewModel.MyApplication
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.regex.Pattern

@AndroidEntryPoint
class Contact() : AppCompatActivity() {
    val binding by lazy { ActivityContactBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }
    var userImageMp: MultipartBody.Part? = null
    private val viewModel1: MultiuserListViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var selectedImagePath: String? = null
    var userType: String = ""
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.imageViewProfile.setImageURI(it)
                selectedImagePath = getFilePathFromUri(it)
                viewModel.userImage = selectedImagePath
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSpinners1()
        with(binding) {
            if (intent.getStringExtra("AddPro").toString() == "AddPro") {
                mobile.visibility = View.GONE
                layoutEmail.hint = "Username"
                title.text = "Add profile"
                subtitle.text = "Enter your details"
                layoutName.visibility = View.VISIBLE
                profile.visibility = View.VISIBLE
                logo.visibility = View.GONE
                spinnerUserType.visibility = View.VISIBLE


            }



            val countryCodeWithPlus: String =
                spinnerCountryCode.selectedCountryCodeWithPlus // Example: "+91"
            imageViewProfile.setOnClickListener { pickImageLauncher.launch("image/*") }
            btnContinue.setOnClickListener {
                userType = binding.spinnerUserType.selectedItem.toString()
                viewModel.email = edtEmail.text.toString().trim()
                viewModel.name = edtName.text.toString().trim()
                viewModel.mobile = countryCodeWithPlus + edtPhone.text.toString().trim()
                viewModel.password = edtPassword.text.toString().trim()
                viewModel.password_confirmation = edtPassword.text.toString().trim()
                if (intent.getStringExtra("AddPro").toString() == "AddPro") {
                    if (edtEmail.text.toString().trim().isEmpty()) {
                        edtEmail.error = "Please enter your username"
                        edtEmail.requestFocus()
                        return@setOnClickListener
                    }
                    if (edtName.text.toString().trim().isEmpty()) {
                        edtName.error = "Please enter your name"
                        edtName.requestFocus()
                        return@setOnClickListener
                    }
                    if (viewModel.password!!.isEmpty()) {
                        edtPassword.error = "Please enter your password"
                        edtPassword.requestFocus()
                        return@setOnClickListener
                    }
                    if (viewModel.password!!.length < 8) {
                        edtPassword.error = "Please enter at least 8 digit password"
                        edtPassword.requestFocus()
                        return@setOnClickListener
                    }
                     userImageMp = selectedImagePath?.let { prepareFilePart("UserImage", it) }
                    if (userImageMp == null) {
                        Toast.makeText(
                            this@Contact,
                            "Please select a valid image",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    if (userType.isEmpty()) {
                        showError1("Please select a user type")
                        return@setOnClickListener
                    }
                } else {
                    if (viewModel.email!!.isEmpty()) {
                        edtEmail.error = "Please enter your email"
                        edtEmail.requestFocus()
                        return@setOnClickListener
                    }
                    if (!isValidEmail(edtEmail.text.toString().trim())) {
                        edtEmail.error = "Please enter a valid email"
                        edtEmail.requestFocus()
                        return@setOnClickListener
                    }
                    if (viewModel.password!!.isEmpty()) {
                        edtPassword.error = "Please enter your password"
                        edtPassword.requestFocus()
                        return@setOnClickListener
                    }
                    if (viewModel.password!!.length < 8) {
                        edtPassword.error = "Please enter at least 8 digit password"
                        edtPassword.requestFocus()
                        return@setOnClickListener
                    }
                    if (viewModel.mobile!!.isEmpty()) {
                        edtPhone.error = "Please enter your phone number"
                        edtPhone.requestFocus()
                        return@setOnClickListener
                    }

                    if (viewModel.mobile!!.length < 10) {
                        edtPhone.error = "Please enter a valid phone number"
                        edtPhone.requestFocus()
                        return@setOnClickListener
                    }
                }

                if (intent.getStringExtra("AddPro").toString() == "AddPro") {
                    viewModel1.createCustomerProfile(
                        viewModel.email.toString(),
                        viewModel.name.toString(), viewModel.password!!, viewModel.user_type.toString(),
                        userImageMp
                    )
                }else{
                    startActivity(Intent(this@Contact,ChooseProfile::class.java))
                }
            }
        }
        viewModel1.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toastic.toastic(
                    context = this@Contact,
                    message = errorMessage,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }
        viewModel1.multiuser.observe(this) { response ->
            if (response?.status == 1){
                Toastic.toastic(
                    context = this@Contact,
                    message = response.message,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
                finish()
            }
        }
        viewModel1.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(this)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val file = File(cacheDir, System.currentTimeMillis().toString() + ".jpg")
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
            }
            if (file.exists()) file.absolutePath else null
        } catch (e: Exception) {
            Log.e("FileError", "Error saving file: ${e.message}")
            null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun prepareFilePart(partName: String, filePath: String?): MultipartBody.Part? {
        if (filePath.isNullOrEmpty()) {
            Log.e("FileError", "File path is null or empty")
            return null
        }

        val file = File(filePath)
        if (!file.exists()) {
            Log.e("FileError", "File not found: $filePath")
            return null
        }

        val requestFile = file.asRequestBody("UserImage/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun setupSpinners1() {
        val userTypeList = listOf("Male", "Female", "Pet")

        // Use requireContext() instead of 'this'
        val adapter =
            ArrayAdapter(this@Contact, android.R.layout.simple_spinner_item, userTypeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerUserType.adapter = adapter

        // Set selected user type
        val selectedIndex = userTypeList.indexOf(viewModel.user_type)
        if (selectedIndex != -1) {
            binding.spinnerUserType.setSelection(selectedIndex)
        }
    }

    // Helper method to show generic errors
    private fun showError1(message: String) {
        SweetAlertDialog(this@Contact, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(message)
            .setConfirmText("Ok")
            .show()
    }
}