package com.tuempresa.stockapp.ui.navigation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.adapters.PurchaseAdapter
import com.tuempresa.stockapp.viewmodels.PurchaseViewModel

class PurchaseListFragment : Fragment() {
    private lateinit var viewModel: PurchaseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PurchaseAdapter
    private lateinit var fabAddPurchase: FloatingActionButton
    private lateinit var fabDeletePurchase: FloatingActionButton
    private val selectedIds = mutableSetOf<Int>()
    private var currentPurchases: List<com.tuempresa.stockapp.models.Purchase> = emptyList()
    private lateinit var textEmptyPurchases: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar with back button
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarPurchaseList)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewPurchases)
        fabAddPurchase = view.findViewById(R.id.fabAddPurchase)
        fabDeletePurchase = view.findViewById(R.id.fabDeletePurchase)
        textEmptyPurchases = view.findViewById(R.id.textEmptyPurchases)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        // Setup ViewModel
        viewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]
        
        // Observe purchases data
        viewModel.purchases.observe(viewLifecycleOwner) { purchases ->
            currentPurchases = purchases
            if (purchases.isNotEmpty()) {
                // create adapter with click and long-click handlers
                adapter = PurchaseAdapter(purchases,
                    onItemClick = { purchase ->
                        if (selectedIds.isEmpty()) {
                            val bundle = Bundle()
                            bundle.putInt("purchaseId", purchase.id)
                            findNavController().navigate(R.id.action_purchaseListFragment_to_purchaseDetailFragment, bundle)
                        } else {
                            // toggle selection on click when in selection mode
                            toggleSelection(purchase.id)
                        }
                    },
                    onItemLongClick = { purchase ->
                        toggleSelection(purchase.id)
                    },
                    onEditClick = { purchase ->
                        // direct edit action from card
                        val bundle = Bundle()
                        bundle.putInt("purchaseId", purchase.id)
                        findNavController().navigate(R.id.action_purchaseListFragment_to_purchaseFormFragment, bundle)
                    },
                    selectedIds = selectedIds
                )
                recyclerView.adapter = adapter
                textEmptyPurchases.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                textEmptyPurchases.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
        
        // Mostrar/ocultar FAB según rol (solo admin puede crear compras)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "")
        if (role != "admin") {
            fabAddPurchase.visibility = View.GONE
            // also hide delete for non-admins
            fabDeletePurchase.visibility = View.GONE
        } else {
            fabAddPurchase.setOnClickListener {
                // Navigate to add purchase form
                findNavController().navigate(R.id.action_purchaseListFragment_to_purchaseFormFragment)
            }
        }
        // Delete FAB behaviour (confirm before deleting)
        fabDeletePurchase.setOnClickListener {
            if (selectedIds.isNotEmpty()) {
                val ids = selectedIds.toList()
                val count = ids.size
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Eliminar $count compra(s)? Esta acción no se puede deshacer.")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar") { _, _ ->
                        deletePurchases(ids)
                    }
                    .show()
            }
        }
        
        // Load purchases
        viewModel.fetchPurchases()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPurchases()
    }

    private fun toggleSelection(id: Int) {
        val wasEmpty = selectedIds.isEmpty()
        if (selectedIds.contains(id)) selectedIds.remove(id) else selectedIds.add(id)
        // update FAB visibility
        fabDeletePurchase.visibility = if (selectedIds.isEmpty()) View.GONE else View.VISIBLE
        // refresh adapter to show selection state for the affected item only
        val pos = currentPurchases.indexOfFirst { it.id == id }
        if (pos >= 0) recyclerView.adapter?.notifyItemChanged(pos)
        // if we just entered selection mode, show a hint
        if (wasEmpty && selectedIds.isNotEmpty()) {
            Snackbar.make(requireView(), "Seleccionado ${selectedIds.size}. Toca otros para seleccionar más o toca el botón eliminar.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun deletePurchases(ids: List<Int>) {
        // disable delete button while processing
        fabDeletePurchase.isEnabled = false
        val failedIds = mutableListOf<Int>()
        var remaining = ids.size
        for (id in ids) {
            viewModel.deletePurchase(id) { success ->
                if (!success) failedIds.add(id) else selectedIds.remove(id)
                remaining -= 1
                if (remaining == 0) {
                    fabDeletePurchase.isEnabled = true
                    // refresh list (observer will update UI)
                    viewModel.fetchPurchases()
                    // update FAB visibility
                    fabDeletePurchase.visibility = if (selectedIds.isEmpty()) View.GONE else View.VISIBLE

                    if (failedIds.isEmpty()) {
                        Snackbar.make(requireView(), "Eliminado(s) correctamente", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val labels = failedIds.map { id ->
                            currentPurchases.find { it.id == id }?.let { "Compra #${it.id}" } ?: id.toString()
                        }

                        // Show a dialog listing failed purchases and allow user to edit one or retry
                        val items = labels.toTypedArray()
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Algunas eliminaciones fallaron")
                            .setItems(items) { _, which ->
                                // navigate to edit the selected failed purchase
                                val failedId = failedIds[which]
                                val bundle = Bundle()
                                bundle.putInt("purchaseId", failedId)
                                findNavController().navigate(R.id.action_purchaseListFragment_to_purchaseFormFragment, bundle)
                            }
                            .setPositiveButton("Reintentar") { _, _ ->
                                deletePurchases(failedIds.toList())
                            }
                            .setNegativeButton("Cerrar", null)
                            .show()
                    }
                }
            }
        }
    }
}