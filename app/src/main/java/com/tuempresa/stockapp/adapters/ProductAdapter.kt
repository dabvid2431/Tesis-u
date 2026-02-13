package com.tuempresa.stockapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.R
import java.util.Locale
import java.text.NumberFormat
import java.text.Normalizer

class ProductAdapter(products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private val allProducts = products.toMutableList()
    private val visibleProducts = products.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productName)
        val price: TextView = view.findViewById(R.id.productPrice)
        val stock: TextView = view.findViewById(R.id.productStock)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = visibleProducts[position]
        holder.name.text = product.name
        // Format price using locale currency
        val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())
        holder.price.text = currency.format(product.price)
        holder.stock.text = product.stock?.toString() ?: "0"
    }
    override fun getItemCount(): Int = visibleProducts.size

    fun submitSearchQuery(query: String) {
        val normalizedQuery = query.normalizeForSearch()
        visibleProducts.clear()
        if (normalizedQuery.isBlank()) {
            visibleProducts.addAll(allProducts)
        } else {
            visibleProducts.addAll(
                allProducts.filter { product ->
                    product.name.normalizeForSearch().contains(normalizedQuery)
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
