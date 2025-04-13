package com.example.drivr
//AIzaSyA0M-20lGPOWGYMyPvdqCXOkzh4TyQjXUs
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // UI Elements
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            // Input Validation
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6 || password.length > 15) {
                Toast.makeText(this, "Password must be 6 to 15 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for email uniqueness
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods?.isNotEmpty() == true) {
                            Toast.makeText(this, "Email is already in use", Toast.LENGTH_SHORT).show()
                        } else {
                            // Check for username uniqueness
                            checkUsernameAndCreateUser(email, username, password)
                        }
                    } else {
                        Toast.makeText(this, "Error checking email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Check for username uniqueness before creating the user
    private fun checkUsernameAndCreateUser(email: String, username: String, password: String) {
        db.collection("Users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Username is unique, create the user
                    createUser(email, username, password)
                } else {
                    Toast.makeText(this, "Username already exists. Please choose a different one.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error checking username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Create the user in Firebase Auth and Firestore
    private fun createUser(email: String, username: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    // User profile information to be stored in Firestore
                    val userInfo = hashMapOf(
                        "email" to email,
                        "username" to username,
                        "createdAt" to System.currentTimeMillis()
                    )

                    // Store user info in Firestore
                    db.collection("Users").document(userId!!)
                        .set(userInfo)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error saving user info: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
