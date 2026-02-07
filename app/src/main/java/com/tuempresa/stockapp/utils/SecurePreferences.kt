package com.tuempresa.stockapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Clase para manejar SharedPreferences cifrados de manera segura
 * Cumple con estándares de seguridad para proteger datos sensibles
 */
object SecurePreferences {
    
    private const val ENCRYPTED_PREFS_FILE = "encrypted_user_prefs"
    
    private var encryptedPrefs: SharedPreferences? = null
    
    /**
     * Obtiene una instancia de SharedPreferences cifrado
     * Usa AES256_GCM para cifrado de valores
     */
    fun getEncryptedPreferences(context: Context): SharedPreferences {
        if (encryptedPrefs == null) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        return encryptedPrefs!!
    }
    
    /**
     * Guarda un token de autenticación de manera segura
     */
    fun saveAuthToken(context: Context, token: String) {
        getEncryptedPreferences(context)
            .edit()
            .putString("auth_token", token)
            .apply()
    }
    
    /**
     * Obtiene el token de autenticación
     */
    fun getAuthToken(context: Context): String? {
        return getEncryptedPreferences(context).getString("auth_token", null)
    }
    
    /**
     * Guarda credenciales de usuario de manera segura
     */
    fun saveUserCredentials(context: Context, email: String, password: String) {
        getEncryptedPreferences(context)
            .edit()
            .putString("user_email", email)
            .putString("user_password", password)
            .apply()
    }
    
    /**
     * Limpia todos los datos cifrados
     */
    fun clearAll(context: Context) {
        getEncryptedPreferences(context)
            .edit()
            .clear()
            .apply()
    }
}
