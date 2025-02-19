package com.example.groomers.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.groomers.R
import com.example.groomers.databinding.ActivityRegisterUserBinding
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.viewModel.MyApplication
import com.groomers.groomersvendor.helper.CustomLoader
import com.groomers.groomersvendor.helper.UploadRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class Registration : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        (application as MyApplication).registerViewModel
    }
    private var selectedImagePath: String? = null
    private var formattedDate = ""
    private var selectedImageUri: Uri? = null
    lateinit var parts: MultipartBody.Part

    val context = this@Registration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Observe loading state


        // Retrieve data from intent

        val mobile = viewModel.mobile ?: ""
        val email = viewModel.email ?: ""
        val password = viewModel.password ?: ""
        val password_confirmation = viewModel.password_confirmation ?: ""
        val role = viewModel.role ?: ""
        val language = viewModel.language ?: ""
        val user_type = viewModel.user_type ?: ""
        val address = viewModel.address ?: ""
        val country = viewModel.country ?: ""
        val state = viewModel.state ?: ""
        val city = viewModel.city ?: ""
        val zipcode = viewModel.zipcode ?: ""
        val latitude = viewModel.latitude ?: ""
        val longitude = viewModel.longitude ?: ""
        val gender = viewModel.gender ?: ""
        val birthdate = viewModel.birthdate ?: ""
        val userImage = viewModel.userImage ?: ""
        val userImageMp = prepareFilePart("shop_agreement", userImage)
        // Observe error message
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })


        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(context)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        })
        // Observe success response
        viewModel.modelRegister.observe(this, Observer { response ->
            // Handle success - Show success message or navigate to another screen
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            // Navigate to the next screen if needed
            startActivity(Intent(context, Login::class.java))
            finish()
        })

        with(binding) {
            imageViewProfile.setOnClickListener { pickImageLauncher.launch("image/*") }
            if (viewModel.user_type != "Pet") {
                tvNameTitle.text = getString(R.string.let_s_get_started_with_the_personal_profile)
                layoutGenderMain.visibility = View.GONE
                tvAge.text = getString(R.string.what_s_your_age)
                edtGender.hint = getString(R.string.enter_gender)
                edtAge.hint = "dd-mm-yyyy"
                edtAgeN.inputType =
                    InputType.TYPE_NUMBER_FLAG_SIGNED // Set the input type to number

            }
            btnContinue.setOnClickListener {
                val inputDate = edtAgeN.text.toString().trim()
                viewModel.name = edtNameN.text.toString().trim()
                viewModel.birthdate = formattedDate
                viewModel.username = edtUserName.text.toString().trim()
                // Define the input and output formats
                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                try {
                    if (viewModel.name!!.isEmpty()) {
                        binding.edtName.error = "Please enter your name"
                        binding.edtName.requestFocus()
                        return@setOnClickListener
                    }
                    if (viewModel.username!!.isEmpty()) {
                        binding.edtUserName.error = "Please enter user name"
                        binding.edtUserName.requestFocus()
                        return@setOnClickListener
                    }
                    if (inputDate.isEmpty()) {
                        binding.edtAge.error = "Please enter user age"
                        binding.edtAge.requestFocus()
                        return@setOnClickListener
                    }
                    // Parse the input date
                    val date = inputFormat.parse(inputDate)

                    // Format the date to the required format
                    formattedDate = outputFormat.format(date)


                    registerUser(
                        "userName1",
                        viewModel.name ?: "",
                        mobile,
                        email,
                        password,
                        password_confirmation,
                        "user",
                        "1",
                        user_type,
                        address,
                        "India",
                        "Telengana",
                        "Hyderabad",
                        "500039",
                        "64387.7",
                        "76347.7",
                        viewModel.gender ?: "",
                        viewModel.birthdate ?: "",
                        userImageMp
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()

                }

            }

        }
    }

    private fun prepareFilePart(partName: String, filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.imageViewProfile.setImageURI(it)
                selectedImagePath = getFilePathFromUri(it)
                viewModel.userImage = selectedImagePath

            }
        }

    private fun registerUser(
        userName: String,
        name: String,
        mobile: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        language: String,
        userType: String,
        address: String,
        country: String,
        state: String,
        city: String,
        zipCode: String,
        latitude: String,
        longitude: String,
        gender: String,
        birthDay: String,
        userImage: MultipartBody.Part
    ) {

        val apiService = ApiServiceProvider.getApiService() // Initialize ApiService
        viewModel.registerUser(
            apiService,userName,
            name, mobile, email, password, confirmPassword, // passwordConfirmation
            role, language, userType,
            address, country, state,city,zipCode,latitude,longitude
            ,gender,birthDay,userImage
        )
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        var filePath: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val fileName = cursor.getString(columnIndex)
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, fileName)
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            filePath = file.absolutePath
        }
        return filePath
    }

}
