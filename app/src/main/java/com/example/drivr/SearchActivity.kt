package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class SearchActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    private lateinit var searchInput: EditText
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchInput = findViewById(R.id.searchUsernameInput)
        searchButton = findViewById(R.id.searchButton)
        db = FirebaseFirestore.getInstance()

        searchButton.setOnClickListener {
            val searchText = searchInput.text.toString().trim()
            if (searchText.isNotEmpty()) {
                db.collection("Users")
                    .whereEqualTo("username", searchText)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            for (document: QueryDocumentSnapshot in result) {
                                val intent = Intent(this, UserProfileActivity::class.java)
                                intent.putExtra("username", document.getString("username"))
                                intent.putExtra("uid", document.id)
                                intent.putExtra("profilePicture", document.getString("profilePicture") ?: "pfp1")

                                val userInfo = buildString {
                                    append("Real Name: ").append(document.getString("realName") ?: "N/A").append("\n")
                                    append("Age: ").append(document.getLong("age")?.toString() ?: "N/A").append("\n")
                                    append("Sex: ").append(document.getString("sex") ?: "N/A").append("\n")
                                    append("Hobbies: ").append(document.getString("hobbies") ?: "N/A").append("\n")
                                    append("Place of Work: ").append(document.getString("placeOfWork") ?: "N/A").append("\n")
                                    append("Car Type: ").append(document.getString("carType") ?: "N/A")
                                }

                                intent.putExtra("userInfo", userInfo)
                                startActivity(intent)
                                break
                            }
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
