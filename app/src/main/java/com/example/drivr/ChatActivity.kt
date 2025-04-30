package com.example.drivr

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var adapter: ChatAdapter

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var receiverId: String
    private var messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        listView = findViewById(R.id.messageList)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        adapter = ChatAdapter(this, messages, auth.currentUser?.uid ?: "")
        listView.adapter = adapter

        receiverId = intent.getStringExtra("receiverId") ?: ""

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }

        listenForMessages()
    }

    private fun getChatId(): String {
        val senderId = auth.currentUser?.uid ?: ""
        return if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"
    }


    private fun sendMessage(messageText: String) {
        val senderId = auth.currentUser?.uid ?: return

        // Fetch sender info from Firestore
        db.collection("Users").document(senderId).get()
            .addOnSuccessListener { doc ->
                val username = doc.getString("username") ?: "Unknown"
                val profilePicture = doc.getString("profilePicture") ?: "pfp1"

                val message = hashMapOf(
                    "senderId" to senderId,
                    "receiverId" to receiverId,
                    "message" to messageText,
                    "timestamp" to System.currentTimeMillis(),
                    "username" to username,
                    "profilePicture" to profilePicture
                )

                db.collection("Messages")
                    .document(getChatId())
                    .collection("Chat")
                    .add(message)
            }
    }


    private fun listenForMessages() {
        val chatId = getChatId()

        db.collection("Messages").document(chatId).collection("Chat")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                messages.clear()
                snapshot?.documents?.forEach { doc ->
                    val msg = ChatMessage(
                        senderId = doc.getString("senderId") ?: "",
                        receiverId = doc.getString("receiverId") ?: "",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                    messages.add(msg)
                }
                adapter.notifyDataSetChanged()
                listView.smoothScrollToPosition(messages.size - 1)
            }
    }
}
