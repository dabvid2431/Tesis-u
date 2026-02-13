package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.adapters.SaleAdapter
import com.tuempresa.stockapp.viewmodels.SaleViewModel

class SaleListFragment : Fragment() {
    private lateinit var viewModel: SaleViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SaleAdapter
    private lateinit var fabAddSale: FloatingActionButton
    private lateinit var textEmptySales: View
    private lateinit var searchInput: EditText
    private var allSales: List<com.tuempresa.stockapp.models.Sale> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_sale_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar. Admin puede volver, vendedor ve icono de menú
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarSaleList)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "")
        toolbar.setNavigationOnClickListener {
            if (role == "admin") {
                findNavController().navigateUp()
            } else {
                // Vendedor: volver al panel principal
                findNavController().navigate(R.id.adminMenuFragment)
            }
        }
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewSales)
        fabAddSale = view.findViewById(R.id.fabAddSale)
        textEmptySales = view.findViewById(R.id.textEmptySales)
        searchInput = view.findViewById(R.id.etSearchSales)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        
        // Setup ViewModel
        viewModel = ViewModelProvider(this)[SaleViewModel::class.java]
        
        // Observe sales data
        viewModel.sales.observe(viewLifecycleOwner) { sales ->
            allSales = sales
            if (sales.isNotEmpty()) {
                adapter = SaleAdapter(sales)
                recyclerView.adapter = adapter
                applyFilter(searchInput.text?.toString().orEmpty())
            } else {
                textEmptySales.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilter(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        
        // Mostrar/ocultar FAB según rol (admin y vendedores pueden crear ventas)
        if (role == "admin" || role == "vendedor") {
            fabAddSale.visibility = View.VISIBLE
            fabAddSale.setOnClickListener {
                // Navigate to add sale form
                findNavController().navigate(R.id.action_saleListFragment_to_saleFormFragment)
            }
        } else {
            fabAddSale.visibility = View.GONE
        }
        
        // Load sales
        viewModel.fetchSales()
    }

    private fun applyFilter(query: String) {
        if (!::adapter.isInitialized) return
        adapter.submitSearchQuery(query)
        val hasItems = adapter.itemCount > 0
        recyclerView.visibility = if (hasItems) View.VISIBLE else View.GONE
        textEmptySales.visibility = if (hasItems) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sale_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                findNavController().navigate(R.id.action_saleListFragment_to_reportsExportFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}