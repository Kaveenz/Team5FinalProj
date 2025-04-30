package com.example.drivr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.drivr.R

class ChatAdapter(
    private val context: Context,
    private val messages: List<ChatMessage>,
    private val currentUserId: String
) : BaseAdapter() {

    override fun getCount(): Int = messages.size

    override fun getItem(position: Int): Any = messages[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val message = messages[position]
        val isSentByUser = message.senderId == currentUserId

        val layoutId = if (isSentByUser) R.layout.item_chat_sent else R.layout.item_chat_received
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)

        val messageText = view.findViewById<TextView>(R.id.messageText)
        val timestampText = view.findViewById<TextView>(R.id.timestampText)
        val usernameText = view.findViewById<TextView>(R.id.usernameText)
        val profileImage = view.findViewById<ImageView>(R.id.profileImage)

        messageText.text = message.message
        timestampText.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
        usernameText.text = message.username

        val avatarResId = context.resources.getIdentifier(
            message.profilePicture,
            "drawable",
            context.packageName
        )
        profileImage.setImageResource(avatarResId)

        return view
    }
}
