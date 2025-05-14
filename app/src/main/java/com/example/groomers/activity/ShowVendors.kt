package com.example.groomers.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.groomers.adapter.VendorsListAdapter
import com.example.groomers.databinding.ActivityShowVendorsBinding
import com.example.groomers.helper.Toastic
import com.example.groomers.sharedpreferences.SessionManager
import com.example.groomers.viewModel.VendorListViewModel
import com.groomers.groomersvendor.helper.CustomLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowVendors : AppCompatActivity() {

    private val viewModel: VendorListViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    private val context = this@ShowVendors
    private val binding by lazy { ActivityShowVendorsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val categoryId = intent.getStringExtra("category_id")

        if (categoryId.isNullOrEmpty()) {
            Toastic.toastic(
                context = this@ShowVendors,
                message = "Invalid category ID",
                duration = Toastic.LENGTH_SHORT,
                type = Toastic.ERROR,
                isIconAnimated = true,
                textColor = Color.RED,
            ).show()
            finish()
            return
        }

        viewModel.getAllVendorsByCategoryId(categoryId)

        // Observe isLoading to show/hide progress
        viewModel.isLoading.observe(context) { isLoading ->
            if (isLoading) {
                CustomLoader.showLoaderDialog(context)
            } else {
                CustomLoader.hideLoaderDialog()
            }
        }

        // Observe the result of the vendor list
        viewModel.vendorList.observe(context) { modelVendors ->
            modelVendors?.let {
                if (modelVendors.status == 1) {
                    binding.rvVendorList.apply {
                        adapter = VendorsListAdapter(modelVendors.result) { selectedCategory ->
                            val selectedUserId = selectedCategory.user_id?.toString() ?: ""
                            if (selectedUserId.isNotEmpty() && selectedUserId != categoryId) {
                                val intent = Intent(context, BookingDetail::class.java).apply {
                                    putExtra("category_id", selectedUserId)
                                }
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        // Observe error messages
        viewModel.errorMessage.observe(context) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toastic.toastic(
                    context = this@ShowVendors,
                    message = errorMessage,
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.ERROR,
                    isIconAnimated = true,
                    textColor = Color.BLUE,
                ).show()
            }
        }
    }
}
