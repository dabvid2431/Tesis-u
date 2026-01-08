package com.tuempresa.stockapp.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
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
        val spinnerProduct: Spinner = view.findViewById(R.id.spinnerLineProduct)
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

        // Setup product spinner
        val productNames = products.map { it.name }
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, productNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerProduct.adapter = adapter

        // If productId is set, select it
        val selectedIndex = products.indexOfFirst { it.id == line.productId }.takeIf { it >= 0 } ?: 0
        holder.spinnerProduct.setSelection(selectedIndex)

        holder.spinnerProduct.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, pos: Int, id: Long) {
                val p = products[pos]
                line.productId = p.id
                line.productName = p.name
                // default price to product sale price if price not manually changed
                if (line.price <= 0.0) {
                    line.price = p.price
                    holder.etPrice.setText(String.format(java.util.Locale.getDefault(), "%.2f", line.price))
                }
                holder.tvSubtotal.text = String.format(java.util.Locale.getDefault(), "%.2f", line.subtotal)
                onChanged()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // No-op: required by AdapterView.OnItemSelectedListener
            }
        })

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
            lines.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
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
        notifyDataSetChanged()
    }
}
