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
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private var formattedDate = ""
    private var selectedImageUri: Uri? = null
    lateinit var parts: MultipartBody.Part

    val context = this@Registration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Observe loading state

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(context)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        })

        // Observe error message
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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

                // Define the input and output formats
                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                try {
                    // Parse the input date
                    val date = inputFormat.parse(inputDate)

                    // Format the date to the required format
                    formattedDate = outputFormat.format(date)

                    // Print the formatted date
                    println(formattedDate)  // Output: 2000-02-22
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()

                }
                viewModel.name = edtNameN.text.toString().trim()
                viewModel.birthdate = formattedDate
                viewModel.username = edtUserName.text.toString().trim()

                Log.e("ViewModelData", "Name " + viewModel.name.toString())
                Log.e("ViewModelData", "userName " + viewModel.username.toString())
                Log.e("ViewModelData", "mobile " + viewModel.mobile.toString())
                Log.e("ViewModelData", "emai l" + viewModel.email.toString())
                Log.e("ViewModelData", "password " + viewModel.password.toString())
                Log.e("ViewModelData", "role " + viewModel.role.toString())
                Log.e("ViewModelData", "profileType " + viewModel.user_type.toString())
                Log.e("ViewModelData", "dob " + viewModel.birthdate.toString())

                // Retrieve data from intent
                val username = viewModel.username ?: ""
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

                // Convert image path to MultipartBody.Part
                val userImageNew = prepareFilePart("UserImage", userImage)
                // Make the API call to register the user
                registerUser(
                    "name",
                    "mobile",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    "email",
                    userImageNew,
                )
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
                handleImageSelection(it)
                //                binding.textImageSelected.text = "Image selected successfully ✅"
//                binding.textImageSelected.visibility = View.VISIBLE
            }
        }

    private fun handleImageSelection(uri: Uri) {
        createMultipartFromUri(context, uri)?.let {
            //parts.add(it)
            parts = it
            viewModel.userImage = parts
        }
    }

    private fun createMultipartFromUri(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val parcelFileDescriptor =
                contentResolver.openFileDescriptor(uri, "r", null) ?: return null
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(
                context.cacheDir,
                contentResolver.getFileName(uri)
            ) // Ensure filename uniqueness
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            val body = UploadRequestBody(file, "image", context)
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                body
            ) // Use "image[]" for multiple uploads
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun ContentResolver.getFileName(uri: Uri): String {
        return query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else "temp_image.jpg"
        } ?: "temp_image.jpg"
    }

    // In your ViewModel
    fun registerUser(
        username: String,
        name: String,
        mobile: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        role: String,
        language: String,
        userType: String,
        address: String,
        country: String,
        state: String,
        city: String,
        zipcode: String,
        latitude: String,
        longitude: String,
        gender: String,
        birthdate: String,
        userImage: MultipartBody.Part
    ) {

        val apiService = ApiServiceProvider.getApiService()
        // Assuming your API method registerUser is a suspend function
        val response = apiService.registerUser(
            username,
            name,
            mobile,
            email,
            password,
            passwordConfirmation,
            role,
            language,
            userType,
            address,
            country,
            state,
            city,
            zipcode,
            latitude,
            longitude,
            gender,
            birthdate,
            userImage
        )

    }


}
