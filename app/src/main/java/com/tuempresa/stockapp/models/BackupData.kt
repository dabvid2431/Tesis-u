package com.tuempresa.stockapp.models

/**
 * Modelo que contiene todos los datos para backup/restore
 */
data class BackupData(
    val timestamp: Long,
    val products: List<Product>,
    val categories: List<Category>,
    val suppliers: List<Supplier>,
    val clients: List<Client>,
    val sales: List<Sale>,
    val purchases: List<Purchase>
)
