package com.example.drivr

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val username: String = "",
    val profilePicture: String = ""  // Like "pfp2"
)
