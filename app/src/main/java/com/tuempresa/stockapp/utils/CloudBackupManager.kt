package com.tuempresa.stockapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gestor de copias de seguridad en Firebase Storage
 * Permite backup y restore de la base de datos en la nube
 */
object CloudBackupManager {
    
    private const val TAG = "CloudBackupManager"
    private const val BACKUP_FOLDER = "backups"
    private val storage = Firebase.storage
    
    /**
     * Realiza un backup de la base de datos a Firebase Storage
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
            // Obtener el archivo de la base de datos
            val dbPath = context.getDatabasePath("stockdb")
            if (!dbPath.exists()) {
                onFailure(Exception("Base de datos no encontrada"))
                return
            }
            
            // Crear nombre único para el backup
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFileName = "stockdb_backup_$timestamp.db"
            
            // Referencia en Firebase Storage
            val storageRef = storage.reference
            val backupRef = storageRef.child("$BACKUP_FOLDER/$backupFileName")
            
            // Subir archivo
            val fileUri = Uri.fromFile(dbPath)
            backupRef.putFile(fileUri)
                .addOnSuccessListener {
                    Log.d(TAG, "Backup exitoso: $backupFileName")
                    onSuccess(backupFileName)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error en backup: ${exception.message}")
                    onFailure(exception)
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    Log.d(TAG, "Progreso del backup: $progress%")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error preparando backup: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Lista todos los backups disponibles en Firebase Storage
     * @param onSuccess Callback con la lista de nombres de backups
     * @param onFailure Callback cuando la operación falla
     */
    fun listBackups(
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference
        val backupRef = storageRef.child(BACKUP_FOLDER)
        
        backupRef.listAll()
            .addOnSuccessListener { listResult ->
                val backupNames = listResult.items.map { it.name }
                Log.d(TAG, "Backups encontrados: ${backupNames.size}")
                onSuccess(backupNames)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error listando backups: ${exception.message}")
                onFailure(exception)
            }
    }
    
    /**
     * Restaura una base de datos desde un backup en Firebase Storage
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
            val storageRef = storage.reference
            val backupRef = storageRef.child("$BACKUP_FOLDER/$backupFileName")
            
            // Archivo temporal para descargar
            val tempFile = File.createTempFile("restore", ".db", context.cacheDir)
            
            backupRef.getFile(tempFile)
                .addOnSuccessListener {
                    try {
                        // Cerrar la base de datos actual (si está abierta)
                        // Nota: Esto debería hacerse desde el ViewModel o Repository
                        
                        // Reemplazar base de datos
                        val dbPath = context.getDatabasePath("stockdb")
                        tempFile.copyTo(dbPath, overwrite = true)
                        tempFile.delete()
                        
                        Log.d(TAG, "Restauración exitosa desde: $backupFileName")
                        onSuccess()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error copiando base de datos: ${e.message}")
                        onFailure(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error descargando backup: ${exception.message}")
                    tempFile.delete()
                    onFailure(exception)
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    Log.d(TAG, "Progreso de restauración: $progress%")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error preparando restauración: ${e.message}")
            onFailure(e)
        }
    }
    
    /**
     * Elimina un backup específico de Firebase Storage
     * @param backupFileName Nombre del archivo de backup a eliminar
     * @param onSuccess Callback cuando la eliminación se completa
     * @param onFailure Callback cuando la eliminación falla
     */
    fun deleteBackup(
        backupFileName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference
        val backupRef = storageRef.child("$BACKUP_FOLDER/$backupFileName")
        
        backupRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "Backup eliminado: $backupFileName")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error eliminando backup: ${exception.message}")
                onFailure(exception)
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
