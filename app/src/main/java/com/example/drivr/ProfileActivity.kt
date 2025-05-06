package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileImageView: ImageView
    private lateinit var musicSpinner: Spinner

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
        musicSpinner = findViewById(R.id.musicSpinner)
        val saveButton = findViewById<Button>(R.id.saveProfileButton)
        val changePfpButton = findViewById<Button>(R.id.changePfpButton)
        val homeButton = findViewById<Button>(R.id.homeButton)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    realName.setText(document.getString("realName") ?: "")
                    age.setText(document.getLong("age")?.toString() ?: "")
                    sex.setText(document.getString("sex") ?: "")
                    hobbies.setText(document.getString("hobbies") ?: "")
                    placeOfWork.setText(document.getString("placeOfWork") ?: "")
                    carType.setText(document.getString("carType") ?: "")

                    val pfpName = document.getString("profilePicture") ?: "pfp1"
                    setProfileImage(pfpName)

                    val selectedMusic = document.getString("selectedMusic") ?: "bmg1"
                    val position = when (selectedMusic) {
                        "bmg1" -> 0
                        "bmg2" -> 1
                        "bmg3" -> 2
                        "bmg4" -> 3
                        "bmg5" -> 4
                        else -> 0
                    }
                    musicSpinner.setSelection(position)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }

        changePfpButton.setOnClickListener {
            val intent = Intent(this, ProfilePictureSelectionActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        homeButton.setOnClickListener {//just added a lil button to bring you back to the hub
            val intent = Intent(this, HubActivity::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            val profileData = mutableMapOf<String, Any?>(
                "realName" to realName.text.toString(),
                "age" to age.text.toString().toIntOrNull(),
                "sex" to sex.text.toString(),
                "hobbies" to hobbies.text.toString(),
                "placeOfWork" to placeOfWork.text.toString(),
                "carType" to carType.text.toString()
            )

            if (userId != null) {
                db.collection("Users").document(userId)
                    .update(profileData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        musicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMusic = when (position) {
                    0 -> "bmg1"
                    1 -> "bmg2"
                    2 -> "bmg3"
                    3 -> "bmg4"
                    4 -> "bmg5"
                    else -> "bmg1"
                }

                if (userId != null) {
                    db.collection("Users").document(userId)
                        .update("selectedMusic", selectedMusic)
                        .addOnSuccessListener {
                            Toast.makeText(this@ProfileActivity, "Music saved! Will play next app launch.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ProfileActivity, "Error saving music preference.", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val selectedPfp = data?.getStringExtra("selectedPfp")
            if (selectedPfp != null) {
                setProfileImage(selectedPfp)
            }
        }
    }

    private fun setProfileImage(pfpName: String) {
        val resourceId = resources.getIdentifier(pfpName, "drawable", packageName)
        profileImageView.setImageResource(resourceId)
    }
}
