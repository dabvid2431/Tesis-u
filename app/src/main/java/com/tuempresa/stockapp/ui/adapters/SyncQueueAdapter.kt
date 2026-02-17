package com.tuempresa.stockapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.offline.SyncQueueEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncQueueAdapter(
    private var items: List<SyncQueueEntity>
) : RecyclerView.Adapter<SyncQueueAdapter.ViewHolder>() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textSyncTitle)
        val status: TextView = view.findViewById(R.id.textSyncStatus)
        val details: TextView = view.findViewById(R.id.textSyncDetails)
        val error: TextView = view.findViewById(R.id.textSyncError)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sync_queue, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.operationType
        holder.status.text = "Estado: ${item.status} · Intentos: ${item.attemptCount}"
        holder.details.text = "Actualizado: ${formatter.format(Date(item.updatedAt))}"
        val hasError = !item.lastError.isNullOrBlank()
        holder.error.visibility = if (hasError) View.VISIBLE else View.GONE
        if (hasError) holder.error.text = "Error: ${item.lastError}"
    }

    override fun getItemCount(): Int = items.size

    fun submit(newItems: List<SyncQueueEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
