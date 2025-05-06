package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class UserProfileActivity : AppCompatActivity() {

    private lateinit var messageButton: Button
    private lateinit var usernameText: TextView
    private lateinit var userInfoText: TextView
    private lateinit var profileImage: ImageView
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        messageButton = findViewById(R.id.messageButton)
        usernameText = findViewById(R.id.userProfileTitle)
        userInfoText = findViewById(R.id.userInfoText)
        profileImage = findViewById(R.id.userProfileImage)

        // Get data from intent
        val username = intent.getStringExtra("username") ?: "User"
        uid = intent.getStringExtra("uid") ?: ""
        val profilePicName = intent.getStringExtra("profilePicture") ?: "pfp1"
        val userInfo = intent.getStringExtra("userInfo") ?: "User Info will be displayed here"

        usernameText.text = username
        userInfoText.text = userInfo
        val resourceId = resources.getIdentifier(profilePicName, "drawable", packageName)
        Glide.with(this).load(resourceId).into(profileImage)

        messageButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverId", uid)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }
}
