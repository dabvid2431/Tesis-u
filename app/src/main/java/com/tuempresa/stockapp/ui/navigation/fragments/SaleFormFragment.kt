package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.stockapp.adapters.SaleLineAdapter
import com.tuempresa.stockapp.models.SaleLine
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.viewmodels.SaleViewModel
import com.tuempresa.stockapp.viewmodels.ClientViewModel
import com.tuempresa.stockapp.viewmodels.ProductViewModel
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.models.Sale

class SaleFormFragment : Fragment() {
    private lateinit var saleViewModel: SaleViewModel
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var spinnerClient: Spinner
    private lateinit var rvSaleLines: RecyclerView
    private lateinit var btnAddLine: Button
    private lateinit var tvSaleTotal: TextView
    private lateinit var btnSave: Button
    private var productList: List<Product> = emptyList()
    private val saleLines = mutableListOf<com.tuempresa.stockapp.models.SaleLine>()
    private lateinit var saleLineAdapter: com.tuempresa.stockapp.adapters.SaleLineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sale_form, container, false)
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            saleViewModel = ViewModelProvider(this)[SaleViewModel::class.java]
            clientViewModel = ViewModelProvider(this)[ClientViewModel::class.java]
            productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

            spinnerClient = view.findViewById(R.id.spinnerSaleClient)
            rvSaleLines = view.findViewById(R.id.rvSaleLines)
            btnAddLine = view.findViewById(R.id.btnAddSaleLine)
            tvSaleTotal = view.findViewById(R.id.tvSaleTotal)
            btnSave = view.findViewById(R.id.btnSaveSale)

            clientViewModel.fetchClients()
            clientViewModel.clients.observe(viewLifecycleOwner) { clients ->
                val items = clients.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerClient.adapter = adapter
            }

            productViewModel.fetchProducts()
            productViewModel.products.observe(viewLifecycleOwner) { resource ->
                if (resource is com.tuempresa.stockapp.utils.Resource.Success) {
                    productList = resource.data ?: emptyList()
                    setupSaleLinesAdapter()
                }
            }

            btnAddLine.setOnClickListener {
                val defaultProduct = productList.firstOrNull()
                saleLines.add(
                    SaleLine(
                        productId = defaultProduct?.id ?: -1,
                        productName = defaultProduct?.name,
                        quantity = 1,
                        price = defaultProduct?.price ?: 0.0
                    )
                )
                saleLineAdapter.notifyItemInserted(saleLines.size - 1)
                updateTotal()
            }

            btnSave.setOnClickListener { saveSale() }
        }

        private fun setupSaleLinesAdapter() {
            saleLineAdapter = SaleLineAdapter(
                products = productList.toMutableList(),
                lines = saleLines,
                onChanged = { updateTotal() }
            )
            rvSaleLines.layoutManager = LinearLayoutManager(requireContext())
            rvSaleLines.adapter = saleLineAdapter
        }

        private fun updateTotal() {
            val total = saleLines.sumOf { it.subtotal }
            tvSaleTotal.text = "Total: %.2f".format(total)
        }

        private fun validateStock(): Boolean {
            // Aquí podrías consultar el stock actual de cada producto y comparar
            // Por simplicidad, solo verifica que la cantidad sea > 0
            for (line in saleLines) {
                if (line.quantity <= 0) return false
            }
            return true
        }

        // computeTotal eliminado: ya no se usa en multi-línea

        private fun saveSale() {
            // Validar cliente
            val selectedClientPos = spinnerClient.selectedItemPosition
            val clients = clientViewModel.clients.value
            if (clients == null || selectedClientPos < 0 || selectedClientPos >= clients.size) {
                Toast.makeText(requireContext(), "Selecciona un cliente", Toast.LENGTH_SHORT).show()
                return
            }
            val clientId = clients[selectedClientPos].id

            // Validar líneas
            if (saleLines.isEmpty()) {
                Toast.makeText(requireContext(), "Agrega al menos una línea de venta", Toast.LENGTH_SHORT).show()
                return
            }
            if (!validateStock()) {
                Toast.makeText(requireContext(), "Cantidad inválida en alguna línea", Toast.LENGTH_SHORT).show()
                return
            }

            // Construir items para el backend
            val itemsList = saleLines.map { line ->
                val productId = productList.find { it.name == line.productName }?.id ?: 0
                mapOf(
                    "productId" to productId,
                    "quantity" to line.quantity,
                    "price" to line.price
                )
            }

            val saleMap: Map<String, Any> = mapOf(
                "clientId" to clientId,
                "items" to itemsList
            )

            saleViewModel.createSaleMap(saleMap) { saved ->
                if (saved != null) {
                    Toast.makeText(requireContext(), "Venta guardada", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar venta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
