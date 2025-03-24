package com.example.groomers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.groomers.R
import com.example.groomers.activity.AddressList
import com.example.groomers.activity.Login
import com.example.groomers.databinding.FragmentProfileBinding
import com.example.groomers.sharedpreferences.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
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
            startActivity(Intent(requireContext(),AddressList::class.java))
        }
        binding.llLogout.setOnClickListener {
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

                            binding.location.text = sessionManager.name
                            binding.sublocation.text = locality

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
}
