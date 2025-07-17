package com.example.groomers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.groomers.R
import com.example.groomers.activity.AddHelp
import com.example.groomers.activity.AddressList
import com.example.groomers.activity.Dashboard
import com.example.groomers.activity.Login
import com.example.groomers.activity.UpdateProfileActivity
import com.example.groomers.activity.Watching
import com.example.groomers.databinding.FragmentProfileBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.ProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class Profile : Fragment() {
    lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var sessionManager: SessionManager
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 100
    private var currentAddress = ""
    private var selectedImageUri: Uri? = null
    private val profileViewModel: ProfileViewModel by viewModels()

    companion object {
        const val REQUEST_CODE_IMAGE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
        binding.llAddress.setOnClickListener {
            startActivity(Intent(requireContext(), AddressList::class.java))
        }
        binding.profileImage.setOnClickListener {
            openImageChooser()
        }
        binding.btnSwitch.setOnClickListener {
            startActivity(Intent(requireContext(), Watching::class.java))
        }
        binding.username.text = sessionManager.username
        binding.initialsLabel.text = getInitials(sessionManager.username)
        binding.ivUpdateProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UpdateProfileActivity::class.java))
        }
        binding.btnSuggestion.setOnClickListener { startActivity(Intent(requireContext(),AddHelp::class.java)) }
        // Load profile and cover images
        Glide.with(requireContext())
            .load("https://groomers.co.in/public/uploads/" + sessionManager.profilePictureUrl)
            .placeholder(R.drawable.user) // Default placeholder
            .into(binding.profileImage)

        binding.btnLogout.setOnClickListener {
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure want to logout?")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setConfirmClickListener { sDialog ->
                    sDialog.cancel()
                    sessionManager.clearSession()
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    requireActivity().finish()
                    startActivity(intent)
                }
                .setCancelClickListener { sDialog ->
                    sDialog.cancel()
                }
                .show()
        }
        profileViewModel.uploadResult.observe(viewLifecycleOwner) { message ->
            if (message == "Uploading...") View.VISIBLE else View.GONE
            message?.let {
                Toastic.toastic(
                    context = requireActivity(),
                    message = it,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.SUCCESS,
                    isIconAnimated = true,
                    textColor = if (false) Color.BLUE else null,
                ).show()
            }
        }

        binding.btnPastOrder.setOnClickListener {
            val navController = findNavController()

            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setLaunchSingleTop(true)
                .build()

            navController.navigate(R.id.appointmentFragment, null, navOptions)
            (requireActivity() as Dashboard).selectBottomTab(Dashboard.ORDER_LIST)

        }


    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrBlank()) return ""

        val parts = name.trim().split("\\s+".toRegex()) // Split by space
        val firstInitial = parts.getOrNull(0)?.firstOrNull()?.uppercaseChar() ?: ""
        val secondInitial = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar() ?: ""

        return "$firstInitial$secondInitial"
    }


    @SuppressLint("SetTextI18n", "LogNotTimber")
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            Log.e(
                                ContentValues.TAG,
                                " addresses[0].latitude${addresses?.get(0)?.latitude}"
                            )
                            Log.e(
                                ContentValues.TAG,
                                " addresses[0].latitude${addresses?.get(0)?.longitude}"
                            )

                            addresses?.get(0)?.getAddressLine(0)

                            val locality = addresses?.get(0)?.locality
                            val countryName = addresses?.get(0)?.countryName
                            val countryCode = addresses?.get(0)?.countryCode
                            val postalCode = addresses?.get(0)?.postalCode
                            val subLocality = addresses?.get(0)?.subLocality
                            val subAdminArea = addresses?.get(0)?.subAdminArea

                            currentAddress = "$subLocality, $locality, $countryName"



                            Log.e(ContentValues.TAG, "locality-$locality")
                            Log.e(ContentValues.TAG, "countryName-$countryName")
                            Log.e(ContentValues.TAG, "countryCode-$countryCode")
                            Log.e(ContentValues.TAG, "postalCode-$postalCode")
                            Log.e(ContentValues.TAG, "subLocality-$subLocality")
                            Log.e(ContentValues.TAG, "subAdminArea-$subAdminArea")

                            Log.e(
                                ContentValues.TAG,
                                " addresses[0].Address${addresses?.get(0)?.getAddressLine(0)}"
                            )

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
        } else {
            askPermission()
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                showConfirmationDialog(uri, true) // Update profile picture

            }
        }
    }

    private fun showConfirmationDialog(uri: Uri, isProfile: Boolean) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(if (isProfile) "Update Profile Picture?" else "Update Cover Picture?")
            .setContentText("Do you want to set this image as your ${if (isProfile) "profile" else "cover"} picture?")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                val file = uriToFile(uri)


                binding.profileImage.setImageURI(uri)
                file?.let { profileViewModel.uploadProfilePicture(it) }

            }
            .setCancelClickListener { sDialog -> sDialog.dismissWithAnimation() }
            .show()
    }

    private fun uriToFile(uri: Uri): File? {
        val resolver = requireContext().contentResolver
        val fileDescriptor = resolver.openFileDescriptor(uri, "r")?.fileDescriptor ?: return null
        val inputStream = FileInputStream(fileDescriptor)
        val file = File(requireContext().cacheDir, "selected_image.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }
}
