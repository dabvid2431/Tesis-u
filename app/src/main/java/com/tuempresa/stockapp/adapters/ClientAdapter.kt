package com.tuempresa.stockapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.models.Client
import com.tuempresa.stockapp.R

class ClientAdapter(private val clients: List<Client>) : RecyclerView.Adapter<ClientAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.clientName)
        val email: TextView = view.findViewById(R.id.clientEmail)
        val phone: TextView = view.findViewById(R.id.clientPhone)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val client = clients[position]
        holder.name.text = client.name
        holder.email.text = client.email ?: "No email"
        holder.phone.text = client.phone ?: "No phone"
    }
    
    override fun getItemCount(): Int = clients.size
}