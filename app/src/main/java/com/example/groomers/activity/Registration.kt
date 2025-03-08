package com.example.groomers.activity

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.groomers.helper.Toastic
import com.example.groomers.retrofit.ApiServiceProvider
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.MyApplication
import com.groomers.groomersvendor.helper.CustomLoader
import com.groomers.groomersvendor.helper.UploadRequestBody
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class Registration : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterUserBinding.inflate(layoutInflater) }
    private val viewModel by lazy { (application as MyApplication).registerViewModel }
    private var selectedImagePath: String? = null
    private var formattedDate = ""
    private var selectedImageUri: Uri? = null
    lateinit var parts: MultipartBody.Part
    private var changeTextColor: Boolean = false
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Observe ViewModel
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) CustomLoader.showLoaderDialog(this)
            else CustomLoader.hideLoaderDialog()
        }

        viewModel.modelRegister.observe(this) {registerModel->
            if (registerModel!=null && registerModel.status==1) {
                sessionManager.userType = viewModel.user_type
                Toastic.toastic(
                    context = this@Registration,
                    message = "Registration Successful",
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (changeTextColor) Color.BLUE else null,
                ).show()
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }
        with(binding) {
//            imageViewProfile.setOnClickListener { pickImageLauncher.launch("image/*") }
            if (viewModel.user_type != "Pet") {
                tvNameTitle.text = getString(R.string.let_s_get_started_with_the_personal_profile)
                layoutGenderMain.visibility = View.GONE
                tvAge.text = getString(R.string.what_s_your_age)
                edtGender.hint = getString(R.string.enter_gender)
                tvAgeN.hint = "dd-mm-yyyy"
                tvAgeN.text = getString(R.string.enter_age)

            } else {
                edtBreedN.visibility = View.VISIBLE
                layoutBreed.visibility = View.GONE
            }
        }
        // Image Picker
        binding.imageViewProfile.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.tvAgeN.setOnClickListener { openDatePickerDialog() }

        binding.btnContinue.setOnClickListener {
            val inputDate = binding.tvAgeN.text.toString().trim()
            viewModel.name = binding.edtNameN.text.toString().trim()
            viewModel.username = binding.edtUserName.text.toString().trim()

            // Validate Inputs
            if (viewModel.name.isNullOrEmpty()) {
                binding.edtName.error = "Please enter your name"
                binding.edtName.requestFocus()
                return@setOnClickListener
            }
            if (viewModel.username.isNullOrEmpty()) {
                binding.edtUserName.error = "Please enter user name"
                binding.edtUserName.requestFocus()
                return@setOnClickListener
            }
            if (inputDate.isEmpty()) {
                binding.tvAgeN.error = "Please enter user age"
                binding.tvAgeN.requestFocus()
                return@setOnClickListener
            }

            // Convert Date Format
            if (viewModel.user_type != "Pet") {
                formattedDate = try {
                    val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    outputFormat.format(inputFormat.parse(inputDate)!!)
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Validate Image Selection
            val userImageMp = selectedImagePath?.let { prepareFilePart("UserImage", it) }
            if (userImageMp == null) {
                Toast.makeText(this, "Please select a valid image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(userImageMp)
        }
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

    private fun registerUser(userImage: MultipartBody.Part) {
        val apiService = ApiServiceProvider.getApiService()
        viewModel.registerUser(
            apiService, viewModel.username ?: "", viewModel.name ?: "", viewModel.mobile ?: "",
            viewModel.email ?: "", viewModel.password ?: "", viewModel.password_confirmation ?: "",
            "user", "1", viewModel.user_type ?: "", viewModel.address ?: "", "5",
            "12", "46", "500039", "64387.7", "76347.7", viewModel.gender ?: "",
            formattedDate, userImage
        )
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

    private fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val formattedDate = "%04d-%02d-%02d".format(year, month + 1, day)
                binding.tvAgeN.text = formattedDate
                viewModel.birthdate = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
