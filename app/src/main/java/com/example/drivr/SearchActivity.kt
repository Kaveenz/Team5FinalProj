package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchUsernameInput = findViewById<EditText>(R.id.searchUsernameInput)
        val searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            val username = searchUsernameInput.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Navigate to UserProfileActivity with the username
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }
}
