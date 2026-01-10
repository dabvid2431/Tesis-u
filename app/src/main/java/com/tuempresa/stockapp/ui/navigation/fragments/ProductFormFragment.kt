package com.tuempresa.stockapp.ui.navigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.Button
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.viewmodels.CategoryViewModel
import com.tuempresa.stockapp.viewmodels.ProductViewModel
import com.tuempresa.stockapp.viewmodels.SupplierViewModel

class ProductFormFragment : Fragment() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var supplierViewModel: SupplierViewModel
    
    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextStock: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerSupplier: Spinner
    private lateinit var buttonSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.title = "Add Product"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Initialize ViewModels
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        supplierViewModel = ViewModelProvider(this)[SupplierViewModel::class.java]
        
        // Initialize views
        editTextName = view.findViewById(R.id.editTextProductName)
        editTextDescription = view.findViewById(R.id.editTextProductDescription)
        editTextPrice = view.findViewById(R.id.editTextProductPrice)
        editTextStock = view.findViewById(R.id.editTextProductStock)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerSupplier = view.findViewById(R.id.spinnerSupplier)
        buttonSave = view.findViewById(R.id.buttonSaveProduct)
        
        // Configure save button
        buttonSave.setOnClickListener {
            saveProduct()
        }
        
        // Cargar categorías y proveedores
        loadCategories()
        loadSuppliers()
    }

    override fun onResume() {
        super.onResume()
        // Mostrar flecha de retroceso en la barra superior
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        requireActivity().actionBar?.setHomeButtonEnabled(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Ocultar flecha de retroceso al salir del fragmento
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().actionBar?.setHomeButtonEnabled(false)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp() // Vuelve al menú principal
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun loadCategories() {
        categoryViewModel.fetchCategories()
        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }
    }
    
    private fun loadSuppliers() {
        supplierViewModel.fetchSuppliers()
        supplierViewModel.suppliers.observe(viewLifecycleOwner) { suppliers ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, suppliers.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSupplier.adapter = adapter
        }
    }
    
    private fun saveProduct() {
        val name = editTextName.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
    // Accept both dot and comma decimal separators (normalize to dot)
    val priceText = editTextPrice.text.toString().trim().replace(',', '.')
        val stockText = editTextStock.text.toString().trim()
        
        // Validaciones
        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        
        val price = priceText.toDoubleOrNull()
        val stock = stockText.toIntOrNull()
        
        if (price == null || price <= 0) {
            Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (stock == null || stock < 0) {
            Toast.makeText(requireContext(), "Stock inválido", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Obtener IDs de categoría y proveedor seleccionados
        val categoryId = getSelectedCategoryId()
        val supplierId = getSelectedSupplierId()
        
        if (categoryId == -1 || supplierId == -1) {
            Toast.makeText(requireContext(), "Selecciona una categoría y un proveedor", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Crear producto con los campos requeridos por el backend
        val productMap = hashMapOf(
            "sku" to name.lowercase().replace(" ", "") + System.currentTimeMillis().toString().takeLast(4),
            "name" to name,
            "brand" to "", // Puedes agregar un campo de marca si lo deseas
            "purchasePrice" to price,
            "salePrice" to price,
            "stock" to stock,
            "categoryId" to categoryId
        )
        productViewModel.createProductMap(productMap) { savedProduct ->
            if (savedProduct != null) {
                Toast.makeText(requireContext(), "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun getSelectedCategoryId(): Int {
        val selectedPosition = spinnerCategory.selectedItemPosition
        if (selectedPosition >= 0) {
            val categories = categoryViewModel.categories.value
            if (categories != null && selectedPosition < categories.size) {
                return categories[selectedPosition].id
            }
        }
        return -1
    }
    
    private fun getSelectedSupplierId(): Int {
        val selectedPosition = spinnerSupplier.selectedItemPosition
        if (selectedPosition >= 0) {
            val suppliers = supplierViewModel.suppliers.value
            if (suppliers != null && selectedPosition < suppliers.size) {
                return suppliers[selectedPosition].id
            }
        }
        return -1
    }
}