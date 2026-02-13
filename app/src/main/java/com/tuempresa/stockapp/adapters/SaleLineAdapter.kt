package com.tuempresa.stockapp.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.models.SaleLine

class SaleLineAdapter(
    private val products: MutableList<Product>,
    val lines: MutableList<SaleLine>,
    private val onChanged: () -> Unit
) : RecyclerView.Adapter<SaleLineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val autoProduct: AutoCompleteTextView = view.findViewById(R.id.autoLineProduct)
        val etQty: EditText = view.findViewById(R.id.etLineQty)
        val etPrice: EditText = view.findViewById(R.id.etLinePrice)
        val tvSubtotal: TextView = view.findViewById(R.id.tvLineSubtotal)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale_line, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lines.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val line = lines[position]

        // Setup searchable products dropdown
        val productNames = products.map { it.name }
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, productNames)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        holder.autoProduct.setAdapter(adapter)
        holder.autoProduct.threshold = 1

        // If productId is set, show selected product name
        val selectedProduct = products.find { it.id == line.productId } ?: products.firstOrNull()
        holder.autoProduct.setText(selectedProduct?.name.orEmpty(), false)
        if (line.productId <= 0 && selectedProduct != null) {
            line.productId = selectedProduct.id
            line.productName = selectedProduct.name
        }

        holder.autoProduct.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            if (pos in products.indices) {
                val p = products[pos]
                line.productId = p.id
                line.productName = p.name
                line.price = p.price
                holder.etPrice.setText(String.format(java.util.Locale.getDefault(), "%.2f", line.price))
                holder.tvSubtotal.text = String.format(java.util.Locale.getDefault(), "%.2f", line.subtotal)
                onChanged()
            }
        }

        holder.autoProduct.setOnClickListener { holder.autoProduct.showDropDown() }

        // quantity watcher
        holder.etQty.setText(line.quantity.toString())
        holder.etQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op: not needed for quantity changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No-op: handling in afterTextChanged
            }
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString().toIntOrNull() ?: 0
                line.quantity = q
                holder.tvSubtotal.text = String.format(java.util.Locale.getDefault(), "%.2f", line.subtotal)
                onChanged()
            }
        })

        // price watcher
        holder.etPrice.setText(if (line.price == 0.0) "" else String.format(java.util.Locale.getDefault(), "%.2f", line.price))
        holder.etPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op: not needed for price changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No-op: handling in afterTextChanged
            }
            override fun afterTextChanged(s: Editable?) {
                val v = s.toString().trim().replace(',', '.').toDoubleOrNull() ?: 0.0
                line.price = v
                holder.tvSubtotal.text = String.format(java.util.Locale.getDefault(), "%.2f", line.subtotal)
                onChanged()
            }
        })

        holder.tvSubtotal.text = String.format(java.util.Locale.getDefault(), "%.2f", line.subtotal)

        holder.btnRemove.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            lines.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, itemCount)
            onChanged()
        }
    }

    fun addLine(line: SaleLine) {
        lines.add(line)
        notifyItemInserted(lines.size - 1)
        onChanged()
    }

    fun setProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyItemRangeChanged(0, itemCount)
    }
}
