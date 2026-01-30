package com.tuempresa.stockapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.models.Notification

class NotificationAdapter(
    private var notifications: List<Notification>,
    private val onMarkAsReadClick: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage = itemView.findViewById<TextView>(R.id.textNotificationMessage)
        private val textDate = itemView.findViewById<TextView>(R.id.textNotificationDate)
        private val buttonMarkAsRead = itemView.findViewById<Button>(R.id.buttonMarkAsRead)

        fun bind(notification: Notification) {
            textMessage.text = notification.message
            textDate.text = if (notification.createdAt != null) {
                notification.createdAt.take(10)
            } else {
                ""
            }
            
            if (notification.read) {
                buttonMarkAsRead.text = "Leído"
                buttonMarkAsRead.isEnabled = false
            } else {
                buttonMarkAsRead.text = "Marcar como leído"
                buttonMarkAsRead.setOnClickListener {
                    onMarkAsReadClick(notification.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    fun updateList(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}
