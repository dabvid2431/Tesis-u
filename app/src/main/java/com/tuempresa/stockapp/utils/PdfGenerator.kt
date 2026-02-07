package com.tuempresa.stockapp.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.models.Sale
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generador de reportes en formato PDF
 * Permite exportar información de productos, ventas y reportes
 */
object PdfGenerator {
    
    private const val REPORTS_DIR = "StockAppReports"
    
    /**
     * Obtiene el directorio de reportes, creándolo si no existe
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
     * Genera un reporte PDF de productos
     * @return Ruta del archivo generado
     */
    fun generateProductsReport(context: Context, products: List<Product>): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Productos_$timestamp.pdf"
        val file = File(getReportsDirectory(context), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Título
        val title = Paragraph("Reporte de Productos")
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
        document.add(title)
        
        // Fecha
        val date = Paragraph("Generado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.RIGHT)
        document.add(date)
        
        document.add(Paragraph("\n"))
        
        // Tabla de productos
        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 30f, 20f, 15f, 15f, 10f)))
            .useAllAvailableWidth()
        
        // Encabezados
        table.addHeaderCell("ID")
        table.addHeaderCell("Nombre")
        table.addHeaderCell("Descripción")
        table.addHeaderCell("Precio Venta")
        table.addHeaderCell("Precio Compra")
        table.addHeaderCell("Stock")
        
        // Datos
        products.forEach { product ->
            table.addCell(product.id.toString())
            table.addCell(product.name ?: "-")
            table.addCell(product.description ?: "-")
            table.addCell("$${String.format("%.2f", product.salePrice)}")
            table.addCell("$${String.format("%.2f", product.purchasePrice)}")
            table.addCell(product.stock.toString())
        }
        
        document.add(table)
        
        // Resumen
        document.add(Paragraph("\n"))
        document.add(Paragraph("Total de productos: ${products.size}"))
        document.add(Paragraph("Total en stock: ${products.sumOf { it.stock }}"))
        
        document.close()
        
        return file.absolutePath
    }
    
    /**
     * Genera un reporte PDF de ventas
     * @return Ruta del archivo generado
     */
    fun generateSalesReport(context: Context, sales: List<Sale>, startDate: String?, endDate: String?): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Ventas_$timestamp.pdf"
        val file = File(getReportsDirectory(context), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Título
        val title = Paragraph("Reporte de Ventas")
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
        document.add(title)
        
        // Período
        if (startDate != null && endDate != null) {
            val period = Paragraph("Período: $startDate - $endDate")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(period)
        }
        
        // Fecha de generación
        val date = Paragraph("Generado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.RIGHT)
        document.add(date)
        
        document.add(Paragraph("\n"))
        
        // Tabla de ventas
        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 20f, 30f, 20f, 20f)))
            .useAllAvailableWidth()
        
        // Encabezados
        table.addHeaderCell("ID")
        table.addHeaderCell("Fecha")
        table.addHeaderCell("Cliente ID")
        table.addHeaderCell("Total")
        table.addHeaderCell("Estado")
        
        // Datos
        var totalRevenue = 0.0
        sales.forEach { sale ->
            table.addCell(sale.id.toString())
            table.addCell(sale.date ?: "-")
            table.addCell(sale.clientId?.toString() ?: "-")
            val saleTotal = sale.total ?: 0.0
            table.addCell("$${String.format("%.2f", saleTotal)}")
            table.addCell("Completada")
            totalRevenue += saleTotal
        }
        
        document.add(table)
        
        // Resumen
        document.add(Paragraph("\n"))
        document.add(Paragraph("Total de ventas: ${sales.size}").setBold())
        document.add(Paragraph("Ingresos totales: $${String.format("%.2f", totalRevenue)}").setBold())
        document.add(Paragraph("Ticket promedio: $${String.format("%.2f", if (sales.isNotEmpty()) totalRevenue / sales.size else 0.0)}"))
        
        document.close()
        
        return file.absolutePath
    }
    
    /**
     * Genera un reporte PDF de stock bajo
     * @return Ruta del archivo generado
     */
    fun generateLowStockReport(context: Context, products: List<Product>, threshold: Int = 10): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "StockBajo_$timestamp.pdf"
        val file = File(getReportsDirectory(context), fileName)
        
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        
        // Título
        val title = Paragraph("Reporte de Stock Bajo")
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
        document.add(title)
        
        val subtitle = Paragraph("Productos con stock menor a $threshold unidades")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
        document.add(subtitle)
        
        document.add(Paragraph("\n"))
        
        // Tabla
        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 35f, 25f, 15f, 15f)))
            .useAllAvailableWidth()
        
        table.addHeaderCell("ID")
        table.addHeaderCell("Nombre")
        table.addHeaderCell("Descripción")
        table.addHeaderCell("Categoría")
        table.addHeaderCell("Stock")
        
        val lowStockProducts = products.filter { it.stock < threshold }
        
        lowStockProducts.forEach { product ->
            table.addCell(product.id.toString())
            table.addCell(product.name ?: "-")
            table.addCell(product.description ?: "-")
            table.addCell(product.categoryId?.toString() ?: "-")
            table.addCell(product.stock.toString())
        }
        
        document.add(table)
        
        // Resumen
        document.add(Paragraph("\n"))
        document.add(Paragraph("Total de productos con stock bajo: ${lowStockProducts.size}").setBold())
        
        document.close()
        
        return file.absolutePath
    }
}
