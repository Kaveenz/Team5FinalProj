package com.example.drivr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notifications: MutableList<NotificationItem>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationText: TextView = itemView.findViewById(R.id.notificationText)
        val notificationTimestamp: TextView = itemView.findViewById(R.id.notificationTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]
        holder.notificationText.text = item.message

        // Format timestamp to readable string
        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(item.timestamp))
        holder.notificationTimestamp.text = formattedTime
    }

    override fun getItemCount(): Int = notifications.size
}
