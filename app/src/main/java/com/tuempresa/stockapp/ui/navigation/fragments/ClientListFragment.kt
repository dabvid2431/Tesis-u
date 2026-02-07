package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.adapters.ClientAdapter
import com.tuempresa.stockapp.viewmodels.ClientViewModel

class ClientListFragment : Fragment() {
    private lateinit var viewModel: ClientViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClientAdapter
    private lateinit var fabAddClient: FloatingActionButton
    private lateinit var textEmptyClients: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar with back button
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarClientList)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewClients)
        fabAddClient = view.findViewById(R.id.fabAddClient)
        textEmptyClients = view.findViewById(R.id.textEmptyClients)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        // Setup ViewModel
        viewModel = ViewModelProvider(this)[ClientViewModel::class.java]
        
        // Observe clients data
        viewModel.clients.observe(viewLifecycleOwner) { clients ->
            if (clients.isNotEmpty()) {
                adapter = ClientAdapter(clients)
                recyclerView.adapter = adapter
                textEmptyClients.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                textEmptyClients.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
        
        // FAB visible para admin y vendedor (ambos pueden crear clientes)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "")
        if (role == "admin" || role == "vendedor") {
            fabAddClient.visibility = View.VISIBLE
            fabAddClient.setOnClickListener {
                // Navigate to add client form
                findNavController().navigate(R.id.action_clientListFragment_to_clientFormFragment)
            }
        } else {
            fabAddClient.visibility = View.GONE
        }
        
        // Load clients
        viewModel.fetchClients()
    }
}