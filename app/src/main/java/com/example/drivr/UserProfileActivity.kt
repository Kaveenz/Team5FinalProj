package com.example.drivr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var messageButton: Button
    private lateinit var addFriendButton: Button
    private lateinit var acceptRequestButton: Button
    private lateinit var declineRequestButton: Button
    private lateinit var usernameText: TextView
    private lateinit var userInfoText: TextView
    private lateinit var profileImage: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var uid: String
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        messageButton = findViewById(R.id.messageButton)
        addFriendButton = findViewById(R.id.addFriendButton)
        acceptRequestButton = findViewById(R.id.acceptRequestButton)
        declineRequestButton = findViewById(R.id.declineRequestButton)
        usernameText = findViewById(R.id.userProfileTitle)
        userInfoText = findViewById(R.id.userInfoText)
        profileImage = findViewById(R.id.userProfileImage)

        currentUserId = auth.currentUser?.uid ?: return
        uid = intent.getStringExtra("uid") ?: return

        val username = intent.getStringExtra("username") ?: "User"
        val profilePicName = intent.getStringExtra("profilePicture") ?: "pfp1"
        val userInfo = intent.getStringExtra("userInfo") ?: "User Info will be displayed here"

        usernameText.text = username
        userInfoText.text = userInfo
        val resourceId = resources.getIdentifier(profilePicName, "drawable", packageName)
        Glide.with(this).load(resourceId).into(profileImage)

        setupButtons()

        messageButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverId", uid)
            startActivity(intent)
        }

        addFriendButton.setOnClickListener {
            sendFriendRequest()
        }

        acceptRequestButton.setOnClickListener {
            acceptFriendRequest()
        }

        declineRequestButton.setOnClickListener {
            declineFriendRequest()
        }
    }

    private fun setupButtons() {
        if (uid == currentUserId) {
            // Hide all buttons if viewing own profile
            messageButton.visibility = View.GONE
            addFriendButton.visibility = View.GONE
            acceptRequestButton.visibility = View.GONE
            declineRequestButton.visibility = View.GONE
            return
        }

        val requestRef = db.collection("Users").document(uid)
            .collection("FriendRequests").document(currentUserId)

        val friendRef = db.collection("Users").document(currentUserId)
            .collection("Friends").document(uid)

        // Check if already friends
        friendRef.get().addOnSuccessListener { friendDoc ->
            if (friendDoc.exists()) {
                addFriendButton.visibility = View.GONE
                messageButton.visibility = View.VISIBLE
            } else {
                // Check if request is received
                db.collection("Users").document(currentUserId)
                    .collection("FriendRequests").document(uid)
                    .get()
                    .addOnSuccessListener { requestDoc ->
                        if (requestDoc.exists()) {
                            acceptRequestButton.visibility = View.VISIBLE
                            declineRequestButton.visibility = View.VISIBLE
                            addFriendButton.visibility = View.GONE
                        } else {
                            // Check if current user already sent a request
                            requestRef.get().addOnSuccessListener { sentDoc ->
                                if (sentDoc.exists()) {
                                    addFriendButton.text = "Request Sent"
                                    addFriendButton.isEnabled = false
                                }
                                addFriendButton.visibility = View.VISIBLE
                            }
                        }
                    }
            }
        }
    }

    private fun sendFriendRequest() {
        val request = mapOf(
            "fromUserId" to currentUserId,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("Users").document(uid)
            .collection("FriendRequests")
            .document(currentUserId)
            .set(request)
            .addOnSuccessListener {
                Toast.makeText(this, "Friend request sent", Toast.LENGTH_SHORT).show()
                addFriendButton.text = "Request Sent"
                addFriendButton.isEnabled = false

                // Add notification
                val notification = mapOf(
                    "type" to "request",
                    "fromUserId" to currentUserId,
                    "message" to "You have a new friend request",
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("Users").document(uid)
                    .collection("Notifications")
                    .add(notification)
            }
    }

    private fun acceptFriendRequest() {
        val currentUserRef = db.collection("Users").document(currentUserId)
        val senderUserRef = db.collection("Users").document(uid)

        val currentUserFriends = currentUserRef.collection("Friends").document(uid)
        val senderUserFriends = senderUserRef.collection("Friends").document(currentUserId)
        val requestRef = currentUserRef.collection("FriendRequests").document(uid)

        currentUserFriends.set(mapOf("friendId" to uid))
        senderUserFriends.set(mapOf("friendId" to currentUserId))

        requestRef.delete().addOnSuccessListener {
            Toast.makeText(this, "Friend request accepted", Toast.LENGTH_SHORT).show()
            acceptRequestButton.visibility = View.GONE
            declineRequestButton.visibility = View.GONE
            messageButton.visibility = View.VISIBLE

            // Notify the original sender
            val notification = mapOf(
                "type" to "accepted",
                "fromUserId" to currentUserId,
                "message" to "Your friend request was accepted",
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("Users").document(uid)
                .collection("Notifications")
                .add(notification)
        }
    }

    private fun declineFriendRequest() {
        db.collection("Users").document(currentUserId)
            .collection("FriendRequests")
            .document(uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Friend request declined", Toast.LENGTH_SHORT).show()
                acceptRequestButton.visibility = View.GONE
                declineRequestButton.visibility = View.GONE
                addFriendButton.visibility = View.VISIBLE
            }
    }
}
