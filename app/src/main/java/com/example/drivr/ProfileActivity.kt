package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val realName = findViewById<EditText>(R.id.realName)
        val age = findViewById<EditText>(R.id.age)
        val sex = findViewById<EditText>(R.id.sex)
        val hobbies = findViewById<EditText>(R.id.hobbies)
        val placeOfWork = findViewById<EditText>(R.id.placeOfWork)
        val carType = findViewById<EditText>(R.id.carType)
        profileImageView = findViewById(R.id.profileImageView)
        val saveButton = findViewById<Button>(R.id.saveProfileButton)
        val changePfpButton = findViewById<Button>(R.id.changePfpButton)

        val userId = auth.currentUser?.uid

        // Load user data from Firestore
        if (userId != null) {
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    realName.setText(document.getString("realName") ?: "")
                    age.setText(document.getLong("age")?.toString() ?: "")
                    sex.setText(document.getString("sex") ?: "")
                    hobbies.setText(document.getString("hobbies") ?: "")
                    placeOfWork.setText(document.getString("placeOfWork") ?: "")
                    carType.setText(document.getString("carType") ?: "")

                    // Load profile picture
                    val pfpName = document.getString("profilePicture") ?: "pfp1"
                    setProfileImage(pfpName)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }

        // Change Profile Picture
        changePfpButton.setOnClickListener {
            val intent = Intent(this, ProfilePictureSelectionActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        // Save Profile Info
        saveButton.setOnClickListener {
            val profileData = mutableMapOf<String, Any?>(
                "realName" to realName.text.toString(),
                "age" to age.text.toString().toIntOrNull(),
                "sex" to sex.text.toString(),
                "hobbies" to hobbies.text.toString(),
                "placeOfWork" to placeOfWork.text.toString(),
                "carType" to carType.text.toString()
            )

            db.collection("Users").document(userId!!)
                .update(profileData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Handle result from Profile Picture Selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val selectedPfp = data?.getStringExtra("selectedPfp")
            if (selectedPfp != null) {
                setProfileImage(selectedPfp)
            }
        }
    }

    // Set Profile Image
    private fun setProfileImage(pfpName: String) {
        val resourceId = resources.getIdentifier(pfpName, "drawable", packageName)
        profileImageView.setImageResource(resourceId)
    }
}
