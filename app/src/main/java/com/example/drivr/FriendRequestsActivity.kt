package com.example.drivr

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendRequestAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val requestList = mutableListOf<FriendRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_requests)

        recyclerView = findViewById(R.id.friendRequestRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FriendRequestAdapter(
            requestList,
            onAccept = { acceptRequest(it) },
            onDecline = { declineRequest(it) }
        )

        recyclerView.adapter = adapter

        loadFriendRequests()
    }

    private fun loadFriendRequests() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("Users").document(currentUserId)
            .collection("FriendRequests")
            .get()
            .addOnSuccessListener { result ->
                requestList.clear()
                for (doc in result) {
                    val senderId = doc.getString("fromUserId") ?: continue
                    val timestamp = doc.getLong("timestamp") ?: 0L

                    // Fetch sender's username
                    db.collection("Users").document(senderId).get().addOnSuccessListener { userDoc ->
                        val senderUsername = userDoc.getString("username") ?: "Unknown"
                        val request = FriendRequest(senderId, senderUsername, timestamp)
                        requestList.add(request)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load friend requests", Toast.LENGTH_SHORT).show()
            }
    }

    private fun acceptRequest(request: FriendRequest) {
        val currentUserId = auth.currentUser?.uid ?: return
        val senderId = request.senderId

        val currentUserRef = db.collection("Users").document(currentUserId)
        val senderUserRef = db.collection("Users").document(senderId)

        val currentUserFriends = currentUserRef.collection("Friends").document(senderId)
        val senderUserFriends = senderUserRef.collection("Friends").document(currentUserId)

        val requestRef = currentUserRef.collection("FriendRequests").document(senderId)

        // Add both users to each other's Friends collection
        currentUserFriends.set(mapOf("friendId" to senderId))
        senderUserFriends.set(mapOf("friendId" to currentUserId))

        // Remove the friend request
        requestRef.delete().addOnSuccessListener {
            requestList.remove(request)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Friend request accepted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun declineRequest(request: FriendRequest) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("Users").document(currentUserId)
            .collection("FriendRequests")
            .document(request.senderId)
            .delete()
            .addOnSuccessListener {
                requestList.remove(request)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Friend request declined", Toast.LENGTH_SHORT).show()
            }
    }
}
