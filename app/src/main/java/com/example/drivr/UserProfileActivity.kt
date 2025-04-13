package com.example.drivr

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userProfileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // UI Elements
        val userInfoText = findViewById<TextView>(R.id.userInfoText)
        userProfileImage = findViewById(R.id.userProfileImage)

        // Get the username from the intent
        val username = intent.getStringExtra("username")

        if (username != null) {
            // Fetch user profile from Firestore
            db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val doc = documents.first()
                        val realName = doc.getString("realName") ?: "N/A"
                        val age = doc.getLong("age")?.toString() ?: "N/A"
                        val sex = doc.getString("sex") ?: "N/A"
                        val hobbies = doc.getString("hobbies") ?: "N/A"
                        val placeOfWork = doc.getString("placeOfWork") ?: "N/A"
                        val carType = doc.getString("carType") ?: "N/A"
                        val pfpName = doc.getString("profilePicture") ?: "pfp1"

                        val userInfo = """
                            Real Name: $realName
                            Age: $age
                            Sex: $sex
                            Hobbies: $hobbies
                            Place of Work: $placeOfWork
                            Car Type: $carType
                        """.trimIndent()

                        userInfoText.text = userInfo
                        setProfileImage(pfpName)
                    } else {
                        userInfoText.text = "User not found."
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            userInfoText.text = "No username provided."
        }
    }

    // Set Profile Image
    private fun setProfileImage(pfpName: String) {
        val resourceId = resources.getIdentifier(pfpName, "drawable", packageName)
        userProfileImage.setImageResource(resourceId)
    }
}
