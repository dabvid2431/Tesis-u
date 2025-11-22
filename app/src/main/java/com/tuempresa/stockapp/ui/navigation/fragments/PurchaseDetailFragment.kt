package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.adapters.PurchaseItemAdapter
import com.tuempresa.stockapp.models.PurchaseItem
import com.tuempresa.stockapp.viewmodels.PurchaseViewModel

class PurchaseDetailFragment : Fragment() {
    private lateinit var viewModel: PurchaseViewModel
    private var purchaseId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        purchaseId = arguments?.getInt("purchaseId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PurchaseViewModel::class.java]

        val tvId = view.findViewById<TextView>(R.id.tvPurchaseId)
        val tvDate = view.findViewById<TextView>(R.id.tvPurchaseDate)
        val tvSupplier = view.findViewById<TextView>(R.id.tvPurchaseSupplier)
        val tvTotal = view.findViewById<TextView>(R.id.tvPurchaseTotal)
        val rvItems = view.findViewById<RecyclerView>(R.id.rvPurchaseItems)
        rvItems.layoutManager = LinearLayoutManager(requireContext())

        viewModel.purchases.observe(viewLifecycleOwner) { purchases ->
            val purchase = purchases.find { it.id == purchaseId }
            if (purchase != null) {
                tvId.text = "Compra #${purchase.id}"
                tvDate.text = "Fecha: ${purchase.date ?: "-"}"
                tvSupplier.text = "Proveedor: ${purchase.supplierId}"
                tvTotal.text = "Total: $%.2f".format(purchase.total)
                rvItems.adapter = PurchaseItemAdapter(purchase.items)
            }
        }
    }
}
