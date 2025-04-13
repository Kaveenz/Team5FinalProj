package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePictureSelectionActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture_selection)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // List of profile pictures and their corresponding IDs
        val profilePictures = listOf(
            R.id.pfp1 to "pfp1",
            R.id.pfp2 to "pfp2",
            R.id.pfp3 to "pfp3",
            R.id.pfp4 to "pfp4",
            R.id.pfp5 to "pfp5",
            R.id.pfp6 to "pfp6"
        )

        // Set click listeners for each profile picture
        for ((viewId, pfpName) in profilePictures) {
            findViewById<ImageView>(viewId).setOnClickListener {
                selectProfilePicture(pfpName)
            }
        }
    }

    // Update the profile picture in Firestore and send the result back to ProfileActivity
    private fun selectProfilePicture(pfpName: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("Users").document(userId)
                .update("profilePicture", pfpName)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()

                    // Send the selected picture name back to the ProfileActivity
                    val resultIntent = Intent()
                    resultIntent.putExtra("selectedPfp", pfpName)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
