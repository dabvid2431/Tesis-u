package com.tuempresa.stockapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.models.Sale
import com.tuempresa.stockapp.R
import java.text.Normalizer
import java.util.Locale

class SaleAdapter(sales: List<Sale>) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {
    private val allSales = sales.toMutableList()
    private val visibleSales = sales.toMutableList()
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.saleId)
        val date: TextView = view.findViewById(R.id.saleDate)
        val seller: TextView = view.findViewById(R.id.saleSeller)
        val total: TextView = view.findViewById(R.id.saleTotal)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = visibleSales[position]
        holder.id.text = "Venta #${sale.id}"
        holder.date.text = sale.date ?: "No date"
        holder.seller.text = "Vendedor: ${sale.sellerName ?: "No registrado"}"
        holder.total.text = "$${String.format(java.util.Locale.getDefault(), "%.2f", sale.total)}"
    }
    
    override fun getItemCount(): Int = visibleSales.size

    fun submitSearchQuery(query: String) {
        val normalizedQuery = query.normalizeForSearch()
        visibleSales.clear()
        if (normalizedQuery.isBlank()) {
            visibleSales.addAll(allSales)
        } else {
            visibleSales.addAll(
                allSales.filter { sale ->
                    val idText = "venta ${sale.id}".normalizeForSearch()
                    val dateText = (sale.date ?: "").normalizeForSearch()
                    val sellerText = (sale.sellerName ?: "").normalizeForSearch()
                    val totalText = String.format(Locale.getDefault(), "%.2f", sale.total).normalizeForSearch()
                    idText.contains(normalizedQuery) ||
                        dateText.contains(normalizedQuery) ||
                        sellerText.contains(normalizedQuery) ||
                        totalText.contains(normalizedQuery)
                }
            )
        }
        notifyDataSetChanged()
    }

    private fun String.normalizeForSearch(): String {
        return Normalizer
            .normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase(Locale.getDefault())
            .trim()
    }
}