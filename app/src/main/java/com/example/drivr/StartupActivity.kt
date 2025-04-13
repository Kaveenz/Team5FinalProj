package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        // Reference to the GIF ImageView
        val gifImageView = findViewById<ImageView>(R.id.gifImageView)

        // Load the GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.rev)
            .into(gifImageView)


        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")


        myRef.setValue("Hello, Firebase!")
            .addOnSuccessListener {
                Toast.makeText(this, "Firebase Initialized!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to connect to Firebase", Toast.LENGTH_SHORT).show()
            }

        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            Toast.makeText(this, "Create Account button clicked", Toast.LENGTH_SHORT).show()
            // Navigate to the Create Account page
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }


        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            Toast.makeText(this, "Login button clicked", Toast.LENGTH_SHORT).show()
            // Navigate to the Login page
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
