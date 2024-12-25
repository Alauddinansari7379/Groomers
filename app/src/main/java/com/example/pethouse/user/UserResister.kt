package com.example.pethouse.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethouse.views.activities.AboutYouActivity
import com.example.pethouse.R
import com.example.pethouse.vendor.RegisterVender
import com.example.pethouse.adapter.TextGridAdapter
import com.google.android.material.textfield.TextInputLayout

class UserResister : AppCompatActivity() {
    private lateinit var adapter: TextGridAdapter // Declare adapter
    private val itemText = mutableListOf<String>() // List to hold dynamic items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue))

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        //val editText: EditText = findViewById(R.id.et_additional)
       // val addButton: Button = findViewById(R.id.btn)

        // Set up RecyclerView with LinearLayoutManager for one item per row
        adapter = TextGridAdapter(itemText)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter



        val addbutton = findViewById<TextInputLayout>(R.id.tipSubUser)

        // Handle Add Button Click
        addbutton!!.setEndIconOnClickListener {
//            val newItem = editText.text.toString().trim()
//            if (newItem.isNotEmpty()) {
//                itemText.add(newItem) // Add new item to the list
//                adapter.notifyItemInserted(itemText.size - 1) // Notify adapter of change
//                editText.text.clear() // Clear the EditText
//            }

            val intent = Intent(this, AboutYouActivity::class.java)
            startActivity(intent)
        }




        // Switch to RegisterVender Activity
        val vanderSwitch = findViewById<Switch>(R.id.checkBox)
        vanderSwitch.setOnClickListener {
            val intent = Intent(this, RegisterVender::class.java)
            startActivity(intent)
        }

        // Switch to User Dashboard
        val submitButton = findViewById<Button>(R.id.btnRegister)
        submitButton.setOnClickListener {
            val intent = Intent(this, SubUserLogin::class.java)
            startActivity(intent)
        }
    }
}
