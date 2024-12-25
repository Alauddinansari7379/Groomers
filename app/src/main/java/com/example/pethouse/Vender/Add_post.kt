package com.example.pethouse.Vender

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pethouse.R

class Add_post : Fragment(R.layout.fragment_add_post) {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var btnAddImage: TextView
    private lateinit var imageViewPreview: ImageView
    private lateinit var btnSubmitPost: Button

    private var selectedImageUri: Uri? = null

    // Define image picker activity result launcher
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageViewPreview.setImageURI(it)
            imageViewPreview.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        imageViewPreview = view.findViewById(R.id.imageViewPreview)
        btnSubmitPost = view.findViewById(R.id.btnSubmit)

        // Button click to add image
        btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Button click to submit post
        btnSubmitPost.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            // Validate input
            if (title.isEmpty()) {
                editTextTitle.error = "Title is required"
            } else if (description.isEmpty()) {
                editTextDescription.error = "Description is required"
            } else {
                // Handle post submission (you can save data to a database or server here)
                submitPost(title, description)
            }
        }
    }

    // Function to handle post submission
    private fun submitPost(title: String, description: String) {
        // If you have an image URI, handle the image upload logic here
        if (selectedImageUri != null) {
            // Do something with the selected image URI, e.g., upload it to a server
        }

        // For now, just show a success message
        // You can replace this with real functionality (e.g., saving to database)
        // For simplicity, just clear the fields for now
        editTextTitle.text.clear()
        editTextDescription.text.clear()
        imageViewPreview.visibility = View.GONE
    }
}
