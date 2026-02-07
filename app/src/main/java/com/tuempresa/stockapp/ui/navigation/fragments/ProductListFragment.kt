package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.adapters.ProductAdapter
import com.tuempresa.stockapp.viewmodels.ProductViewModel
import com.tuempresa.stockapp.utils.Resource

class ProductListFragment : Fragment() {
    private lateinit var viewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        progressBar = view.findViewById(R.id.progressBarProducts)
        errorText = view.findViewById(R.id.textErrorProducts)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        viewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    errorText.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    val products = resource.data ?: emptyList()
                    if (products.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        errorText.visibility = View.VISIBLE
                        errorText.text = "No hay productos"
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        errorText.visibility = View.GONE
                        adapter = ProductAdapter(products)
                        recyclerView.adapter = adapter
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    errorText.text = resource.message ?: "Error desconocido"
                }
            }
        }
        viewModel.fetchProducts()
        
        // Verificar stock bajo automáticamente
        val apiService = com.tuempresa.stockapp.api.RetrofitClient.instance
        apiService.getNotifications().enqueue(object : retrofit2.Callback<List<com.tuempresa.stockapp.models.Notification>> {
            override fun onResponse(
                call: retrofit2.Call<List<com.tuempresa.stockapp.models.Notification>>,
                response: retrofit2.Response<List<com.tuempresa.stockapp.models.Notification>>
            ) {
                // Notificaciones cargadas, nada que hacer aquí
            }

            override fun onFailure(call: retrofit2.Call<List<com.tuempresa.stockapp.models.Notification>>, t: Throwable) {
                // Error al cargar notificaciones, ignorar
            }
        })

        // FAB para agregar producto usando Navigation Component (visible sólo para admin)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddProduct)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "")
        if (role != "admin") {
            fab.visibility = View.GONE
        } else {
            fab.setOnClickListener {
                findNavController().navigate(R.id.productFormFragment)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                findNavController().navigate(R.id.action_productListFragment_to_reportsExportFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
