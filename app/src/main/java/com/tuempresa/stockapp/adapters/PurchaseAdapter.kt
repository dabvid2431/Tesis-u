package com.tuempresa.stockapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.models.Purchase
import com.tuempresa.stockapp.R
import java.text.Normalizer
import java.util.Locale

class PurchaseAdapter(
    purchases: List<Purchase>,
    private val onItemClick: (Purchase) -> Unit,
    private val onItemLongClick: (Purchase) -> Unit,
    private val onEditClick: (Purchase) -> Unit,
    private val selectedIds: Set<Int>
) : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {
    private val allPurchases = purchases.toMutableList()
    private val visiblePurchases = purchases.toMutableList()
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.purchaseId)
        val date: TextView = view.findViewById(R.id.purchaseDate)
        val total: TextView = view.findViewById(R.id.purchaseTotal)
        val btnEdit: android.widget.ImageButton? = view.findViewById(R.id.btnEditPurchase)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purchase = visiblePurchases[position]
        holder.id.text = "Compra #${purchase.id}"
        holder.date.text = purchase.date ?: "No date"
        holder.total.text = "$${String.format(java.util.Locale.getDefault(), "%.2f", purchase.total)}"
        holder.itemView.setOnClickListener { onItemClick(purchase) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(purchase)
            true
        }
        holder.btnEdit?.setOnClickListener {
            onEditClick(purchase)
        }
        // Visual feedback for selection
        holder.itemView.alpha = if (selectedIds.contains(purchase.id)) 0.5f else 1f
    }
    
    override fun getItemCount(): Int = visiblePurchases.size

    fun submitSearchQuery(query: String) {
        val normalizedQuery = query.normalizeForSearch()
        visiblePurchases.clear()
        if (normalizedQuery.isBlank()) {
            visiblePurchases.addAll(allPurchases)
        } else {
            visiblePurchases.addAll(
                allPurchases.filter { purchase ->
                    val idText = "compra ${purchase.id}".normalizeForSearch()
                    val dateText = (purchase.date ?: "").normalizeForSearch()
                    val totalText = String.format(Locale.getDefault(), "%.2f", purchase.total).normalizeForSearch()
                    idText.contains(normalizedQuery) ||
                        dateText.contains(normalizedQuery) ||
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