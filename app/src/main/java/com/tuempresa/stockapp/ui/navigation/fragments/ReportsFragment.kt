package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.viewmodels.ReportsViewModel

class ReportsFragment : Fragment() {
    private val viewModel: ReportsViewModel by viewModels()
    
    private lateinit var cardSalesReport: MaterialCardView
    private lateinit var cardTopProducts: MaterialCardView
    private lateinit var cardLowStock: MaterialCardView
    private lateinit var cardStockMovements: MaterialCardView
    
    private lateinit var containerSalesReport: LinearLayout
    private lateinit var containerTopProducts: LinearLayout
    private lateinit var containerLowStock: LinearLayout
    private lateinit var containerStockMovements: LinearLayout
    
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        loadReports()
        observeViewModel()
    }
    
    private fun initViews(view: View) {
        cardSalesReport = view.findViewById(R.id.cardSalesReport)
        cardTopProducts = view.findViewById(R.id.cardTopProducts)
        cardLowStock = view.findViewById(R.id.cardLowStock)
        cardStockMovements = view.findViewById(R.id.cardStockMovements)
        
        containerSalesReport = view.findViewById(R.id.containerSalesReport)
        containerTopProducts = view.findViewById(R.id.containerTopProducts)
        containerLowStock = view.findViewById(R.id.containerLowStock)
        containerStockMovements = view.findViewById(R.id.containerStockMovements)
        
        progressBar = view.findViewById(R.id.progressBar)
        
        cardSalesReport.setOnClickListener { expandSalesReport() }
        cardTopProducts.setOnClickListener { expandTopProducts() }
        cardLowStock.setOnClickListener { expandLowStock() }
        cardStockMovements.setOnClickListener { expandStockMovements() }
    }
    
    private fun loadReports() {
        viewModel.fetchSalesReport()
        viewModel.fetchTopProducts()
        viewModel.fetchLowStock()
        viewModel.fetchStockMovements()
    }
    
    private fun observeViewModel() {
        viewModel.salesReportData.observe(viewLifecycleOwner) { report ->
            progressBar.visibility = View.GONE
            containerSalesReport.removeAllViews()
            containerSalesReport.addView(createSalesReportView(report))
        }
        
        viewModel.topProductsData.observe(viewLifecycleOwner) { products ->
            containerTopProducts.removeAllViews()
            products.forEach { product ->
                containerTopProducts.addView(createProductRowView(product))
            }
        }
        
        viewModel.lowStockData.observe(viewLifecycleOwner) { products ->
            containerLowStock.removeAllViews()
            products.forEach { product ->
                containerLowStock.addView(createProductRowView(product))
            }
        }
        
        viewModel.stockMovementsData.observe(viewLifecycleOwner) { movements ->
            containerStockMovements.removeAllViews()
            movements.forEach { movement ->
                containerStockMovements.addView(createMovementRowView(movement))
            }
        }
    }
    
    private fun expandSalesReport() {
        val isVisible = containerSalesReport.visibility == View.VISIBLE
        containerSalesReport.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
    
    private fun expandTopProducts() {
        val isVisible = containerTopProducts.visibility == View.VISIBLE
        containerTopProducts.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
    
    private fun expandLowStock() {
        val isVisible = containerLowStock.visibility == View.VISIBLE
        containerLowStock.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
    
    private fun expandStockMovements() {
        val isVisible = containerStockMovements.visibility == View.VISIBLE
        containerStockMovements.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
    
    private fun createSalesReportView(report: Map<String, Any>): View {
        val container = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 8)
        }
        
        val totalSalesText = TextView(requireContext()).apply {
            text = "Total ventas: ${report["totalSales"]}"
            textSize = 14f
        }
        
        val totalRevenueText = TextView(requireContext()).apply {
            text = "Ingreso total: \$${report["totalRevenue"]}"
            textSize = 14f
        }
        
        container.addView(totalSalesText)
        container.addView(totalRevenueText)
        return container
    }
    
    private fun createProductRowView(product: Map<String, Any>): View {
        val row = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 4, 8, 4)
        }
        
        val name = when {
            product["name"] != null -> product["name"].toString()
            product["Product"] is Map<*, *> -> (product["Product"] as Map<*, *>)["name"]?.toString() ?: "Desconocido"
            else -> "Desconocido"
        }
        
        val quantity = when {
            product["stock"] != null -> product["stock"].toString()
            product["quantity"] != null -> product["quantity"].toString()
            product["totalSold"] != null -> product["totalSold"].toString()
            else -> "0"
        }
        
        val nameText = TextView(requireContext()).apply {
            text = name
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val quantityText = TextView(requireContext()).apply {
            text = quantity
            textSize = 12f
        }
        
        row.addView(nameText)
        row.addView(quantityText)
        return row
    }
    
    private fun createMovementRowView(movement: Map<String, Any>): View {
        val row = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(8, 4, 8, 4)
        }
        
        val type = movement["type"]?.toString() ?: "UNKNOWN"
        
        val productName = when {
            movement["productName"] != null -> movement["productName"].toString()
            movement["Product"] is Map<*, *> -> (movement["Product"] as Map<*, *>)["name"]?.toString() ?: "Desconocido"
            else -> "Desconocido"
        }
        
        val quantity = movement["quantity"]?.toString() ?: "0"
        
        val typeText = TextView(requireContext()).apply {
            text = "Tipo: $type"
            textSize = 12f
        }
        
        val productText = TextView(requireContext()).apply {
            text = "Producto: $productName"
            textSize = 12f
        }
        
        val quantityText = TextView(requireContext()).apply {
            text = "Cantidad: $quantity"
            textSize = 12f
        }
        
        row.addView(typeText)
        row.addView(productText)
        row.addView(quantityText)
        return row
    }
}
