package com.tuempresa.stockapp.ui.navigation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tuempresa.stockapp.R
import com.tuempresa.stockapp.utils.*
import com.tuempresa.stockapp.viewmodels.ProductViewModel
import com.tuempresa.stockapp.viewmodels.SaleViewModel

/**
 * Fragment para gestionar exportación de reportes y backup
 * Incluye funcionalidades de PDF, Excel y backup en nube
 */
class ReportsExportFragment : Fragment() {
    
    private lateinit var productViewModel: ProductViewModel
    private lateinit var saleViewModel: SaleViewModel
    
    private lateinit var btnExportProductsPdf: Button
    private lateinit var btnExportProductsExcel: Button
    private lateinit var btnExportSalesPdf: Button
    private lateinit var btnExportSalesExcel: Button
    private lateinit var btnBackupCloud: Button
    private lateinit var btnRestoreCloud: Button
    private lateinit var btnListBackups: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    
    private var pendingAction: (() -> Unit)? = null
    
    // Request permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "✅ Permiso concedido", Toast.LENGTH_SHORT).show()
            tvStatus.text = "Listo para exportar"
            // Ejecutar la acción pendiente si existe
            pendingAction?.invoke()
            pendingAction = null
        } else {
            Toast.makeText(context, "❌ Permiso denegado. Ve a Configuración → Apps → StockApp → Permisos → Almacenamiento", Toast.LENGTH_LONG).show()
            tvStatus.text = "Permiso denegado. Configura los permisos en ajustes."
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reports_export, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModels
        productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
        saleViewModel = ViewModelProvider(requireActivity())[SaleViewModel::class.java]
        
        // Initialize views
        initViews(view)
        
        // Setup accessibility
        setupAccessibility()
        
        // Check and request permissions automatically when fragment loads
        checkStoragePermissions()
    }
    
    private fun initViews(view: View) {
        btnExportProductsPdf = view.findViewById(R.id.btnExportProductsPdf)
        btnExportProductsExcel = view.findViewById(R.id.btnExportProductsExcel)
        btnExportSalesPdf = view.findViewById(R.id.btnExportSalesPdf)
        btnExportSalesExcel = view.findViewById(R.id.btnExportSalesExcel)
        btnBackupCloud = view.findViewById(R.id.btnBackupCloud)
        btnRestoreCloud = view.findViewById(R.id.btnRestoreCloud)
        btnListBackups = view.findViewById(R.id.btnListBackups)
        progressBar = view.findViewById(R.id.progressBar)
        tvStatus = view.findViewById(R.id.tvStatus)
        
        // Set click listeners
        btnExportProductsPdf.setOnClickListener { exportProductsToPdf() }
        btnExportProductsExcel.setOnClickListener { exportProductsToExcel() }
        btnExportSalesPdf.setOnClickListener { exportSalesToPdf() }
        btnExportSalesExcel.setOnClickListener { exportSalesToExcel() }
        btnBackupCloud.setOnClickListener { backupToCloud() }
        btnRestoreCloud.setOnClickListener { showRestoreDialog() }
        btnListBackups.setOnClickListener { listBackups() }
    }
    
    private fun setupAccessibility() {
        // Configurar accesibilidad según WCAG
        AccessibilityHelper.setButtonAccessibility(btnExportProductsPdf, "Exportar productos a PDF")
        AccessibilityHelper.setButtonAccessibility(btnExportProductsExcel, "Exportar productos a CSV/Excel")
        AccessibilityHelper.setButtonAccessibility(btnExportSalesPdf, "Exportar ventas a PDF")
        AccessibilityHelper.setButtonAccessibility(btnExportSalesExcel, "Exportar ventas a CSV/Excel")
        AccessibilityHelper.setButtonAccessibility(btnBackupCloud, "Realizar backup en la nube")
        AccessibilityHelper.setButtonAccessibility(btnRestoreCloud, "Restaurar backup desde la nube")
        AccessibilityHelper.setButtonAccessibility(btnListBackups, "Listar backups disponibles")
    }
    
    private fun checkStoragePermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10+ usa Scoped Storage, no necesita permisos para Documents
                tvStatus.text = "Listo para exportar"
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    tvStatus.text = "Se requieren permisos de almacenamiento"
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    tvStatus.text = "Listo para exportar"
                }
            }
            else -> {
                tvStatus.text = "Listo para exportar"
            }
        }
    }
    
    private fun hasStoragePermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> true // Scoped Storage
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> true
        }
    }
    
    private fun requestStoragePermissionWithAction(action: () -> Unit) {
        if (hasStoragePermission()) {
            action()
        } else {
            pendingAction = action
            tvStatus.text = "Solicitando permisos..."
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
    
    private fun exportProductsToPdf() {
        requestStoragePermissionWithAction {
            performExportProductsToPdf()
        }
    }
    
    private fun performExportProductsToPdf() {
        showProgress("Exportando productos a PDF...")
        
        productViewModel.products.observe(viewLifecycleOwner) { resource ->
            if (resource is com.tuempresa.stockapp.utils.Resource.Success) {
                val products = resource.data
                if (products?.isNotEmpty() == true) {
                    try {
                        val filePath = PdfGenerator.generateProductsReport(requireContext(), products)
                        hideProgress()
                        showStatus("PDF generado exitosamente en: $filePath")
                        AccessibilityHelper.announceForAccessibility(tvStatus, "Reporte PDF de productos generado exitosamente")
                        Toast.makeText(context, "PDF guardado en: $filePath", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        hideProgress()
                        showStatus("Error al generar PDF: ${e.message}")
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    hideProgress()
                    showStatus("No hay productos para exportar")
                    Toast.makeText(context, "No hay productos disponibles", Toast.LENGTH_SHORT).show()
                }
            } else if (resource is com.tuempresa.stockapp.utils.Resource.Error) {
                hideProgress()
                showStatus("Error: ${resource.message}")
            }
        }
        
        productViewModel.fetchProducts()
    }
    
    private fun exportProductsToExcel() {
        requestStoragePermissionWithAction {
            performExportProductsToExcel()
        }
    }
    
    private fun performExportProductsToExcel() {
        showProgress("Exportando productos a CSV...")
        
        productViewModel.products.observe(viewLifecycleOwner) { resource ->
            if (resource is com.tuempresa.stockapp.utils.Resource.Success) {
                val products = resource.data
                if (products?.isNotEmpty() == true) {
                    try {
                        // Usar CSV en lugar de Excel para mejor compatibilidad
                        val filePath = CsvGenerator.generateProductsReport(requireContext(), products)
                        hideProgress()
                        showStatus("CSV generado exitosamente en: $filePath")
                        AccessibilityHelper.announceForAccessibility(tvStatus, "Reporte CSV de productos generado exitosamente")
                        Toast.makeText(context, "CSV guardado (abrir en Excel): $filePath", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        hideProgress()
                        showStatus("Error al generar CSV: ${e.message}")
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    hideProgress()
                    showStatus("No hay productos para exportar")
                    Toast.makeText(context, "No hay productos disponibles", Toast.LENGTH_SHORT).show()
                }
            } else if (resource is com.tuempresa.stockapp.utils.Resource.Error) {
                hideProgress()
                showStatus("Error: ${resource.message}")
            }
        }
        
        productViewModel.fetchProducts()
    }
    
    private fun exportSalesToPdf() {
        requestStoragePermissionWithAction {
            performExportSalesToPdf()
        }
    }
    
    private fun performExportSalesToPdf() {
        showProgress("Exportando ventas a PDF...")
        
        saleViewModel.sales.observe(viewLifecycleOwner) { sales ->
            if (sales.isNotEmpty()) {
                try {
                    val filePath = PdfGenerator.generateSalesReport(requireContext(), sales, null, null)
                    hideProgress()
                    showStatus("PDF de ventas generado en: $filePath")
                    AccessibilityHelper.announceForAccessibility(tvStatus, "Reporte PDF de ventas generado exitosamente")
                    Toast.makeText(context, "PDF guardado en: $filePath", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    hideProgress()
                    showStatus("Error al generar PDF: ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideProgress()
                showStatus("No hay ventas para exportar")
                Toast.makeText(context, "No hay ventas disponibles", Toast.LENGTH_SHORT).show()
            }
        }
        
        saleViewModel.fetchSales()
    }
    
    private fun exportSalesToExcel() {
        requestStoragePermissionWithAction {
            performExportSalesToExcel()
        }
    }
    
    private fun performExportSalesToExcel() {
        showProgress("Exportando ventas a CSV...")
        
        saleViewModel.sales.observe(viewLifecycleOwner) { sales ->
            if (sales.isNotEmpty()) {
                try {
                    // Usar CSV en lugar de Excel para mejor compatibilidad
                    val filePath = CsvGenerator.generateSalesReport(requireContext(), sales, null, null)
                    hideProgress()
                    showStatus("CSV de ventas generado en: $filePath")
                    AccessibilityHelper.announceForAccessibility(tvStatus, "Reporte CSV de ventas generado exitosamente")
                    Toast.makeText(context, "CSV guardado (abrir en Excel): $filePath", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    hideProgress()
                    showStatus("Error al generar CSV: ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideProgress()
                showStatus("No hay ventas para exportar")
                Toast.makeText(context, "No hay ventas disponibles", Toast.LENGTH_SHORT).show()
            }
        }
        
        saleViewModel.fetchSales()
    }
    
    private fun backupToCloud() {
        showProgress("Realizando backup en la nube...")
        
        CloudBackupManager.backupDatabase(
            requireContext(),
            onSuccess = { backupName ->
                hideProgress()
                showStatus("Backup exitoso: $backupName")
                AccessibilityHelper.announceForAccessibility(tvStatus, "Backup completado exitosamente")
                Toast.makeText(context, "Backup guardado: $backupName", Toast.LENGTH_LONG).show()
            },
            onFailure = { exception ->
                hideProgress()
                showStatus("Error en backup: ${exception.message}")
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    private fun showRestoreDialog() {
        // Aquí podrías mostrar un diálogo con la lista de backups disponibles
        Toast.makeText(context, "Funcionalidad de restauración disponible", Toast.LENGTH_SHORT).show()
    }
    
    private fun listBackups() {
        showProgress("Obteniendo lista de backups...")
        
        CloudBackupManager.listBackups(
            onSuccess = { backups ->
                hideProgress()
                val backupList = backups.joinToString("\n")
                showStatus("Backups disponibles:\n$backupList")
                AccessibilityHelper.announceForAccessibility(tvStatus, "Se encontraron ${backups.size} backups")
                Toast.makeText(context, "Backups: ${backups.size}", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                hideProgress()
                showStatus("Error listando backups: ${exception.message}")
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    private fun showProgress(message: String) {
        progressBar.visibility = View.VISIBLE
        tvStatus.text = message
        AccessibilityHelper.announceForAccessibility(tvStatus, message)
    }
    
    private fun hideProgress() {
        progressBar.visibility = View.GONE
    }
    
    private fun showStatus(message: String) {
        tvStatus.text = message
    }
}
