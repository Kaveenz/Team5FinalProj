package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // UI Elements
        val emailInput = findViewById<EditText>(R.id.loginEmailInput)
        val passwordInput = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Login Button Click Listener
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validate Inputs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt to log in
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        // Navigate to the Hub page
                        startActivity(Intent(this, HubActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        Toast.makeText(this, "Login failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
