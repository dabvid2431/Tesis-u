package com.tuempresa.stockapp.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gestor de copias de seguridad en almacenamiento local
 * Descarga todos los datos del backend y los guarda como JSON
 */
object CloudBackupManager {
    
    private const val TAG = "CloudBackupManager"
    private const val BACKUP_FOLDER = "StockAppBackups"
    private val gson = Gson()
    
    /**
     * Obtiene el directorio de backups, creándolo si no existe
     */
    private fun getBackupsDirectory(context: Context): File {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val backupsDir = File(documentsDir, BACKUP_FOLDER)
        if (!backupsDir.exists()) {
            backupsDir.mkdirs()
        }
        return backupsDir
    }
    
    /**
     * Realiza un backup de todos los datos del backend al almacenamiento local
     * @param context Contexto de la aplicación
     * @param onSuccess Callback cuando el backup se completa exitosamente
     * @param onFailure Callback cuando el backup falla
     */
    fun backupDatabase(
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            Log.d(TAG, "Iniciando descarga de datos del backend...")
            
            // Contenedor para todos los datos
            var products: List<Product>? = null
            var categories: List<Category>? = null
            var suppliers: List<Supplier>? = null
            var clients: List<Client>? = null
            var sales: List<Sale>? = null
            var purchases: List<Purchase>? = null
            
            var completedRequests = 0
            val totalRequests = 6
            
            fun checkCompletion() {
                completedRequests++
                if (completedRequests == totalRequests) {
                    // Todos los datos descargados, crear backup
                    val backupData = BackupData(
                        timestamp = System.currentTimeMillis(),
                        products = products ?: emptyList(),
                        categories = categories ?: emptyList(),
                        suppliers = suppliers ?: emptyList(),
                        clients = clients ?: emptyList(),
                        sales = sales ?: emptyList(),
                        purchases = purchases ?: emptyList()
                    )
                    
                    saveBackupToLocalStorage(context, backupData, onSuccess, onFailure)
                }
            }
            
            // Descargar productos
            RetrofitClient.instance.getProducts().enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    products = response.body() ?: emptyList()
                    Log.d(TAG, "Productos descargados: ${products?.size}")
                    checkCompletion()
                }
                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Log.e(TAG, "Error descargando productos: ${t.message}")
                    onFailure(Exception("Error descargando productos: ${t.message}"))
                }
            })
            
            // Descargar categorías
            RetrofitClient.instance.getCategories().enqueue(object : Callback<List<Category>> {
                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    categories = response.body() ?: emptyList()
                    Log.d(TAG, "Categorías descargadas: ${categories?.size}")
                    checkCompletion()
                }
                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Log.e(TAG, "Error descargando categorías: ${t.message}")
                    onFailure(Exception("Error descargando categorías: ${t.message}"))
                }
            })
            
            // Descargar proveedores
            RetrofitClient.instance.getSuppliers().enqueue(object : Callback<List<Supplier>> {
                override fun onResponse(call: Call<List<Supplier>>, response: Response<List<Supplier>>) {
                    suppliers = response.body() ?: emptyList()
                    Log.d(TAG, "Proveedores descargados: ${suppliers?.size}")
                    checkCompletion()
                }
                override fun onFailure(call: Call<List<Supplier>>, t: Throwable) {
                    Log.e(TAG, "Error descargando proveedores: ${t.message}")
                    onFailure(Exception("Error descargando proveedores: ${t.message}"))
                }
            })
            
            // Descargar clientes
            RetrofitClient.instance.getClients().enqueue(object : Callback<List<Client>> {
                override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                    clients = response.body() ?: emptyList()
                    Log.d(TAG, "Clientes descargados: ${clients?.size}")
                    checkCompletion()
                }
                override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                    Log.e(TAG, "Error descargando clientes: ${t.message}")
                    onFailure(Exception("Error descargando clientes: ${t.message}"))
                }
            })
            
            // Descargar ventas
            RetrofitClient.instance.getSales().enqueue(object : Callback<List<Sale>> {
                override fun onResponse(call: Call<List<Sale>>, response: Response<List<Sale>>) {
                    sales = response.body() ?: emptyList()
                    Log.d(TAG, "Ventas descargadas: ${sales?.size}")
                    checkCompletion()
                }
                override fun onFailure(call: Call<List<Sale>>, t: Throwable) {
                    Log.e(TAG, "Error descargando ventas: ${t.message}")
                    onFailure(Exception("Error descargando ventas: ${t.message}"))
                }
            })
            
            // Descargar compras
            RetrofitClient.instance.getPurchases().enqueue(object : Callback<List<Purchase>> {
                override fun onResponse(call: Call<List<Purchase>>, response: Response<List<Purchase>>) {
                    purchases = response.body() ?: emptyList()
                    Log.d(TAG, "Compras descargadas: ${purchases?.size}")
                    checkCompletion()
                }
                override fun onFailure(call: Call<List<Purchase>>, t: Throwable) {
                    Log.e(TAG, "Error descargando compras: ${t.message}")
                    onFailure(Exception("Error descargando compras: ${t.message}"))
                }
            })
            
        } catch (e: Exception) {
            Log.e(TAG, "Error preparando backup: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Guarda los datos de backup en almacenamiento local
     */
    private fun saveBackupToLocalStorage(
        context: Context,
        backupData: BackupData,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Convertir a JSON
            val jsonData = gson.toJson(backupData)
            
            // Crear nombre único para el backup
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFileName = "stockapp_backup_$timestamp.json"
            
            // Guardar en almacenamiento local
            val backupsDir = getBackupsDirectory(context)
            val backupFile = File(backupsDir, backupFileName)
            backupFile.writeText(jsonData)
            
            Log.d(TAG, "✅ Backup guardado exitosamente: ${backupFile.absolutePath}")
            onSuccess(backupFileName)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando backup: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Lista todos los backups disponibles en almacenamiento local
     * @param onSuccess Callback con la lista de nombres de backups
     * @param onFailure Callback cuando la operación falla
     */
    fun listBackups(
        context: Context,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val backupsDir = getBackupsDirectory(context)
            
            if (!backupsDir.exists()) {
                Log.d(TAG, "Directorio de backups no existe, creándolo...")
                backupsDir.mkdirs()
                onSuccess(emptyList())
                return
            }
            
            val backupFiles = backupsDir.listFiles { file ->
                file.isFile && file.extension == "json"
            }?.map { it.name }?.sortedDescending() ?: emptyList()
            
            Log.d(TAG, "Backups encontrados: ${backupFiles.size}")
            onSuccess(backupFiles)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error listando backups: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Restaura datos desde un backup en almacenamiento local
     * ADVERTENCIA: Esto eliminará todos los datos actuales del backend
     * @param context Contexto de la aplicación
     * @param backupFileName Nombre del archivo de backup
     * @param onSuccess Callback cuando la restauración se completa
     * @param onFailure Callback cuando la restauración falla
     */
    fun restoreDatabase(
        context: Context,
        backupFileName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val backupsDir = getBackupsDirectory(context)
            val backupFile = File(backupsDir, backupFileName)
            
            if (!backupFile.exists()) {
                onFailure(Exception("Backup no encontrado: $backupFileName"))
                return
            }
            
            // Leer JSON
            val jsonData = backupFile.readText()
            val backupData = gson.fromJson(jsonData, BackupData::class.java)
            
            Log.d(TAG, "Backup cargado. Restaurando datos...")
            Log.d(TAG, "Productos: ${backupData.products.size}")
            Log.d(TAG, "Categorías: ${backupData.categories.size}")
            Log.d(TAG, "Proveedores: ${backupData.suppliers.size}")
            Log.d(TAG, "Clientes: ${backupData.clients.size}")
            Log.d(TAG, "Ventas: ${backupData.sales.size}")
            Log.d(TAG, "Compras: ${backupData.purchases.size}")
            
            // Restaurar datos al backend
            restoreDataToBackend(backupData, onSuccess, onFailure)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error preparando restauración: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Restaura los datos al backend usando el endpoint /api/backup/restore
     */
    private fun restoreDataToBackend(
        backupData: BackupData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d(TAG, "Enviando datos al backend para restauración...")
        
        RetrofitClient.instance.restoreBackup(backupData).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Restauración completada exitosamente")
                    val result = response.body()
                    Log.d(TAG, "Respuesta del servidor: $result")
                    onSuccess()
                } else {
                    val error = "Error del servidor: ${response.code()} - ${response.message()}"
                    Log.e(TAG, error)
                    onFailure(Exception(error))
                }
            }
            
            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e(TAG, "❌ Error restaurando al backend: ${t.message}")
                onFailure(Exception("Error de conexión: ${t.message}"))
            }
        })
    }
    
    /**
     * Elimina un backup específico del almacenamiento local
     * @param backupFileName Nombre del archivo de backup a eliminar
     * @param onSuccess Callback cuando la eliminación se completa
     * @param onFailure Callback cuando la eliminación falla
     */
    fun deleteBackup(
        context: Context,
        backupFileName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val backupsDir = getBackupsDirectory(context)
            val backupFile = File(backupsDir, backupFileName)
            
            if (backupFile.exists()) {
                if (backupFile.delete()) {
                    Log.d(TAG, "Backup eliminado: $backupFileName")
                    onSuccess()
                } else {
                    onFailure(Exception("No se pudo eliminar el archivo"))
                }
            } else {
                onFailure(Exception("Backup no encontrado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando backup: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Realiza un backup automático si han pasado más de X días desde el último
     * @param context Contexto de la aplicación
     * @param daysSinceLastBackup Días desde el último backup para ejecutar uno nuevo
     */
    fun autoBackupIfNeeded(
        context: Context,
        daysSinceLastBackup: Int = 7,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val prefs = context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)
        val lastBackupTime = prefs.getLong("last_backup_timestamp", 0)
        val currentTime = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000
        
        if (currentTime - lastBackupTime > (daysSinceLastBackup * dayInMillis)) {
            backupDatabase(context, { backupName ->
                prefs.edit().putLong("last_backup_timestamp", currentTime).apply()
                onSuccess(backupName)
            }, onFailure)
        } else {
            Log.d(TAG, "No es necesario hacer backup automático todavía")
        }
    }
}

