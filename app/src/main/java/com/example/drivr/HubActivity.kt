package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        // Profile Button
        val profileButton = findViewById<Button>(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Search Button
        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Map Button
        val mapButton = findViewById<Button>(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        // Settings Button (Placeholder)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            // Future settings activity or feature
        }

        val viewFriendRequestsButton = findViewById<Button>(R.id.viewFriendRequestsButton)
        viewFriendRequestsButton.setOnClickListener {
            val intent = Intent(this, FriendRequestsActivity::class.java)
            startActivity(intent)
        }

        val chatHistButton = findViewById<Button>(R.id.chatsHistButton)
        chatHistButton.setOnClickListener{
            val intent = Intent(this, ChatHistory::class.java)
            startActivity(intent)
        }

    }
}
