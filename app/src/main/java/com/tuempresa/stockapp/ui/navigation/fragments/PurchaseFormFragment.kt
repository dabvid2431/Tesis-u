package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.adapters.PurchaseLineAdapter
import com.tuempresa.stockapp.models.PurchaseLine
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.viewmodels.PurchaseViewModel
import com.tuempresa.stockapp.viewmodels.SupplierViewModel
import com.tuempresa.stockapp.viewmodels.ProductViewModel
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.models.Purchase

class PurchaseFormFragment : Fragment() {
    private lateinit var purchaseViewModel: PurchaseViewModel
    private lateinit var supplierViewModel: SupplierViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var spinnerSupplier: Spinner
    private lateinit var btnSave: Button
    private var productList: List<Product> = emptyList()

    private lateinit var recyclerView: RecyclerView
    private lateinit var purchaseLineAdapter: PurchaseLineAdapter
    private val purchaseLines = mutableListOf<PurchaseLine>()
    private var isEditMode: Boolean = false
    private var editPurchaseId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if opened for editing
        val pid = arguments?.getInt("purchaseId", -1) ?: -1
        if (pid >= 0) {
            isEditMode = true
            editPurchaseId = pid
        }

        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]
        supplierViewModel = ViewModelProvider(this)[SupplierViewModel::class.java]
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        spinnerSupplier = view.findViewById(R.id.spinnerPurchaseSupplier)
        btnSave = view.findViewById(R.id.btnSavePurchase)

        supplierViewModel.fetchSuppliers()
        supplierViewModel.suppliers.observe(viewLifecycleOwner) { suppliers ->
            val items = suppliers.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSupplier.adapter = adapter
            // if editing, select supplier when suppliers are available
            if (isEditMode) {
                // try to find the purchase (if already fetched)
                loadPurchaseIfAvailable()
            }
        }

        productViewModel.fetchProducts()
        productViewModel.products.observe(viewLifecycleOwner) { resource ->
            if (resource is com.tuempresa.stockapp.utils.Resource.Success) {
                productList = resource.data ?: emptyList()
                purchaseLineAdapter.setProducts(productList)
                // if editing, try to populate lines (products may be needed to match by name)
                if (isEditMode) loadPurchaseIfAvailable()
            } else {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        }

        // If editing, fetch purchases so we can load the one to edit
        if (isEditMode) {
            purchaseViewModel.fetchPurchases()
            purchaseViewModel.purchases.observe(viewLifecycleOwner) {
                loadPurchaseIfAvailable()
            }
        }

        btnSave.setOnClickListener { savePurchase() }

        recyclerView = view.findViewById(R.id.rvPurchaseLines)
        purchaseLineAdapter = PurchaseLineAdapter(productList.toMutableList(), purchaseLines) {
            computeTotal() // Recalculate total when lines change
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = purchaseLineAdapter

        val btnAddLine: Button = view.findViewById(R.id.btnAddPurchaseLine)
        btnAddLine.setOnClickListener {
            addPurchaseLine()
        }
    }

    private fun computeTotal() {
        val total = purchaseLines.sumOf { it.quantity * it.price }
        val root = view
        val tvTotal: TextView? = root?.findViewById(R.id.tvTotal)
        tvTotal?.text = String.format(java.util.Locale.getDefault(), "Total: %.2f", total)
    }

    private fun savePurchase() {
        val supplierId = getSelectedSupplierId() ?: return
        if (!validatePurchaseLines()) return
        if (!validateProductList()) return

        // Construir lista para backend
        val itemsList = purchaseLines.map {
            mapOf(
                "productId" to it.productId,
                "quantity" to it.quantity,
                "price" to it.price
            )
        }

        val purchaseMap: Map<String, Any> = mapOf(
            "supplierId" to supplierId,
            "items" to itemsList
        )

        if (isEditMode && editPurchaseId >= 0) {
            performUpdate(supplierId)
        } else {
            performCreate(purchaseMap)
        }
    }

    private fun getSelectedSupplierId(): Int? {
        val selectedSupplierPos = spinnerSupplier.selectedItemPosition
        val suppliers = supplierViewModel.suppliers.value
        if (suppliers == null || selectedSupplierPos < 0 || selectedSupplierPos >= suppliers.size) {
            Toast.makeText(requireContext(), "Selecciona un proveedor", Toast.LENGTH_SHORT).show()
            return null
        }
        return suppliers[selectedSupplierPos].id
    }

    private fun validatePurchaseLines(): Boolean {
        if (purchaseLines.isEmpty()) {
            Toast.makeText(requireContext(), "Agrega al menos una línea de producto", Toast.LENGTH_SHORT).show()
            return false
        }
        for ((index, line) in purchaseLines.withIndex()) {
            if (line.quantity <= 0) {
                Toast.makeText(requireContext(), "La cantidad en la línea ${index + 1} debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return false
            }
            if (line.price <= 0) {
                Toast.makeText(requireContext(), "El precio en la línea ${index + 1} debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun validateProductList(): Boolean {
        if (productList.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona un producto", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun performUpdate(supplierId: Int) {
        val itemsForUpdate = purchaseLines.map { line ->
            val pname = line.productName ?: productList.find { it.id == line.productId }?.name ?: ""
            com.tuempresa.stockapp.models.PurchaseItem(pname, line.quantity, line.price)
        }
        val total = purchaseLines.sumOf { it.subtotal }
        val purchaseToUpdate = Purchase(id = editPurchaseId, supplierId = supplierId, date = null, total = total, items = itemsForUpdate)
        purchaseViewModel.updatePurchase(editPurchaseId, purchaseToUpdate) { saved ->
            if (saved != null) {
                Toast.makeText(requireContext(), "Compra actualizada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al actualizar compra", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performCreate(purchaseMap: Map<String, Any>) {
        purchaseViewModel.createPurchaseMap(purchaseMap) { saved ->
            if (saved != null) {
                Toast.makeText(requireContext(), "Compra guardada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al guardar compra", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPurchaseIfAvailable() {
        if (!isEditMode || editPurchaseId < 0) return
        val purchases = purchaseViewModel.purchases.value ?: return
        val target = purchases.find { it.id == editPurchaseId } ?: return

        // select supplier if available
        val suppliers = supplierViewModel.suppliers.value
        if (suppliers != null) {
            val idx = suppliers.indexOfFirst { it.id == target.supplierId }.takeIf { it >= 0 } ?: 0
            spinnerSupplier.setSelection(idx)
        }

        // populate lines from purchase items
        val prevSize = purchaseLines.size
        purchaseLines.clear()
        for (item in target.items) {
            // try to match product by name to get productId
            val matched = productList.find { it.name == item.productName }
            val pid = matched?.id ?: 0
            val pl = PurchaseLine(productId = pid, productName = item.productName, quantity = item.quantity, price = item.price)
            purchaseLines.add(pl)
        }
        if (::purchaseLineAdapter.isInitialized) {
            val newSize = purchaseLines.size
            if (prevSize == 0) {
                if (newSize > 0) purchaseLineAdapter.notifyItemRangeInserted(0, newSize)
            } else {
                purchaseLineAdapter.notifyDataSetChanged() // fallback when previous content existed
            }
        }
        computeTotal()
    }

    private fun addPurchaseLine() {
        val defaultProduct = productList.firstOrNull()
        val newLine = PurchaseLine(productId = defaultProduct?.id ?: 0, quantity = 1, price = defaultProduct?.price ?: 0.0)
        purchaseLines.add(newLine)
        purchaseLineAdapter.notifyItemInserted(purchaseLines.size - 1)
        computeTotal()
    }
}
