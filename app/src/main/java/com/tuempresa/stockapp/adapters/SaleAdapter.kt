package com.tuempresa.stockapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.models.Sale
import com.tuempresa.stockapp.R

class SaleAdapter(private val sales: List<Sale>) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.saleId)
        val date: TextView = view.findViewById(R.id.saleDate)
        val total: TextView = view.findViewById(R.id.saleTotal)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = sales[position]
        holder.id.text = "Venta #${sale.id}"
        holder.date.text = sale.date ?: "No date"
        holder.total.text = "$${String.format(java.util.Locale.getDefault(), "%.2f", sale.total)}"
    }
    
    override fun getItemCount(): Int = sales.size
}