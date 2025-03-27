package com.example.groomers.activity

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groomers.R
import com.example.groomers.databinding.ActivityUpdateProfileBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class UpdateProfileActivity : AppCompatActivity() {

    // Lazy ViewBinding
    private val binding by lazy { ActivityUpdateProfileBinding.inflate(layoutInflater) }

    @Inject
    lateinit var sessionManager: SessionManager

    // ViewModel Injection using Hilt
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set Gender Options in Spinner
        setupGenderSpinner()
        binding.imgBack.setOnClickListener {
            finish()
        }
        // Handle Back Button Click
        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle Birthdate Picker
        binding.etBirthdate.setOnClickListener {
            openDatePickerDialog()
        }

        // Handle Update Button Click
        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }

        // Observe LiveData from ViewModel
        observeViewModel()
    }


    // Setup Gender Spinner
    private fun setupGenderSpinner() {
        val genderOptions = arrayOf("Male", "Female", "Pet")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
    }

    // Observe ViewModel for Profile Updates
    private fun observeViewModel() {
        viewModel.modelEditProfile.observe(this) { result ->
            result?.let {
                if (result.status == 1) {
                    sessionManager.username = result.result.name
                     Toastic.toastic(
                        context = this@UpdateProfileActivity,
                        message = "Profile Updated Successfully!",
                        duration = Toastic.LENGTH_SHORT,
                        type = Toastic.SUCCESS,
                        isIconAnimated = true,
                        textColor = if (false) Color.BLUE else null,
                    ).show()
                    finish()
                }
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                 Toastic.toastic(
                    context = this@UpdateProfileActivity,
                    message = it,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnUpdate.isEnabled = !isLoading
        }
    }

    private fun updateProfile() {
        val name = binding.etName.text.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val gender = binding.spinnerGender.selectedItem.toString()
        val birthdate = binding.etBirthdate.text.toString().trim()


        // Validate each field and show error directly in EditText
        if (name.isEmpty()) {
            binding.etName.error = "Please enter your name"
            binding.etName.requestFocus()
            return
        }

        if (mobile.isEmpty()) {
            binding.etMobile.error = "Please enter your mobile number"
            binding.etMobile.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Please enter your email"
            binding.etEmail.requestFocus()
            return
        }

        if (address.isEmpty()) {
            binding.etAddress.error = "Please enter your address"
            binding.etAddress.requestFocus()
            return
        }

        if (birthdate=="Select birth date") {
            binding.etBirthdate.error = "Please select your birth date"
            binding.etBirthdate.requestFocus()
            return
        }



        // Fetch Token from SessionManager
        val token = sessionManager.accessToken ?: ""
        if (token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            Toastic.toastic(
                context = this@UpdateProfileActivity,
                message = "Session expired. Please log in again.",
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.ERROR,
                isIconAnimated = true,
                textColor = if (false) Color.BLUE else null,
            ).show()
            return
        }

        // Call ViewModel to Update Profile
        viewModel.updateUserDetails(token, name, mobile, email, address, gender, birthdate)

        // Observe success or error
        observeViewModel()
    }


    // Open Date Picker to Select Birthdate
    private fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                binding.etBirthdate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}