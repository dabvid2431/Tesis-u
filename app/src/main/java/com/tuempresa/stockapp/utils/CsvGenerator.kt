package com.tuempresa.stockapp.utils

import android.content.Context
import android.os.Environment
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.models.Sale
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generador de reportes en formato CSV
 * Alternativa ligera a Excel, compatible con todas las versiones de Android
 * Los archivos CSV se pueden abrir en Excel, Google Sheets y LibreOffice
 */
object CsvGenerator {
    
    private const val REPORTS_DIR = "StockAppReports"
    private const val CSV_SEPARATOR = ","
    
    /**
     * Obtiene el directorio de reportes
     */
    private fun getReportsDirectory(context: Context): File {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val reportsDir = File(documentsDir, REPORTS_DIR)
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }
        return reportsDir
    }
    
    /**
     * Escapa valores CSV (maneja comas y comillas)
     */
    private fun escapeCsvValue(value: String): String {
        return if (value.contains(CSV_SEPARATOR) || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
    
    /**
     * Genera un reporte CSV de productos
     * @return Ruta del archivo generado
     */
    fun generateProductsReport(context: Context, products: List<Product>): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Productos_$timestamp.csv"
        val file = File(getReportsDirectory(context), fileName)
        
        FileWriter(file).use { writer ->
            // Encabezados
            writer.append("ID,Nombre,Descripción,Precio Compra,Precio Venta,Stock,Categoría ID,Proveedor ID\n")
            
            // Datos
            products.forEach { product ->
                writer.append(product.id.toString())
                writer.append(CSV_SEPARATOR)
                writer.append(escapeCsvValue(product.name ?: ""))
                writer.append(CSV_SEPARATOR)
                writer.append(escapeCsvValue(product.description ?: ""))
                writer.append(CSV_SEPARATOR)
                writer.append(product.purchasePrice?.toString() ?: "0")
                writer.append(CSV_SEPARATOR)
                writer.append(product.salePrice?.toString() ?: "0")
                writer.append(CSV_SEPARATOR)
                writer.append(product.stock.toString())
                writer.append(CSV_SEPARATOR)
                writer.append(product.categoryId?.toString() ?: "")
                writer.append(CSV_SEPARATOR)
                writer.append(product.supplierId?.toString() ?: "")
                writer.append("\n")
            }
            
            // Resumen
            writer.append("\n")
            writer.append("RESUMEN\n")
            writer.append("Total de productos,$${products.size}\n")
            writer.append("Total en stock,$${products.sumOf { it.stock }}\n")
        }
        
        return file.absolutePath
    }
    
    /**
     * Genera un reporte CSV de ventas
     * @return Ruta del archivo generado
     */
    fun generateSalesReport(context: Context, sales: List<Sale>, startDate: String?, endDate: String?): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Ventas_$timestamp.csv"
        val file = File(getReportsDirectory(context), fileName)
        
        FileWriter(file).use { writer ->
            // Información del reporte
            writer.append("Reporte de Ventas\n")
            if (startDate != null && endDate != null) {
                writer.append("Período: $startDate - $endDate\n")
            }
            writer.append("Generado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}\n")
            writer.append("\n")
            
            // Encabezados
            writer.append("ID,Fecha,Cliente ID,Total,Estado\n")
            
            // Datos
            var totalRevenue = 0.0
            sales.forEach { sale ->
                writer.append(sale.id?.toString() ?: "")
                writer.append(CSV_SEPARATOR)
                writer.append(escapeCsvValue(sale.date ?: ""))
                writer.append(CSV_SEPARATOR)
                writer.append(sale.clientId?.toString() ?: "")
                writer.append(CSV_SEPARATOR)
                val saleTotal = sale.total ?: 0.0
                writer.append(String.format("%.2f", saleTotal))
                writer.append(CSV_SEPARATOR)
                writer.append("Completada")
                writer.append("\n")
                totalRevenue += saleTotal
            }
            
            // Resumen
            writer.append("\n")
            writer.append("RESUMEN\n")
            writer.append("Total de ventas,$${sales.size}\n")
            writer.append("Ingresos totales,$${String.format("%.2f", totalRevenue)}\n")
            writer.append("Ticket promedio,$${String.format("%.2f", if (sales.isNotEmpty()) totalRevenue / sales.size else 0.0)}\n")
        }
        
        return file.absolutePath
    }
    
    /**
     * Genera un reporte CSV de stock bajo
     * @return Ruta del archivo generado
     */
    fun generateLowStockReport(context: Context, products: List<Product>, threshold: Int = 10): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "StockBajo_$timestamp.csv"
        val file = File(getReportsDirectory(context), fileName)
        
        val lowStockProducts = products.filter { it.stock < threshold }.sortedBy { it.stock }
        
        FileWriter(file).use { writer ->
            // Título
            writer.append("Reporte de Stock Bajo\n")
            writer.append("Productos con stock menor a $threshold unidades\n")
            writer.append("Generado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}\n")
            writer.append("\n")
            
            // Encabezados
            writer.append("ID,Nombre,Descripción,Stock Actual,Precio Venta,Categoría ID\n")
            
            // Datos
            lowStockProducts.forEach { product ->
                writer.append(product.id.toString())
                writer.append(CSV_SEPARATOR)
                writer.append(escapeCsvValue(product.name ?: ""))
                writer.append(CSV_SEPARATOR)
                writer.append(escapeCsvValue(product.description ?: ""))
                writer.append(CSV_SEPARATOR)
                writer.append(product.stock.toString())
                writer.append(CSV_SEPARATOR)
                writer.append(String.format("%.2f", product.salePrice ?: 0.0))
                writer.append(CSV_SEPARATOR)
                writer.append(product.categoryId?.toString() ?: "")
                writer.append("\n")
            }
            
            // Resumen
            writer.append("\n")
            writer.append("RESUMEN\n")
            writer.append("Total de productos con stock bajo,$${lowStockProducts.size}\n")
        }
        
        return file.absolutePath
    }
}
