package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.adapters.SupplierAdapter
import com.tuempresa.stockapp.viewmodels.SupplierViewModel

class SupplierListFragment : Fragment() {
    private lateinit var viewModel: SupplierViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SupplierAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_supplier_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSuppliers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SupplierViewModel::class.java]
        viewModel.suppliers.observe(viewLifecycleOwner) { suppliers ->
            adapter = SupplierAdapter(suppliers)
            recyclerView.adapter = adapter
        }
        viewModel.fetchSuppliers()
    }
}
