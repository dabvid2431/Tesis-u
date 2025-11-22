package com.tuempresa.stockapp.models

data class Purchase(
    val id: Int,
    val supplierId: Int,
    val date: String?,
    val total: Double,
    val items: List<PurchaseItem> = emptyList()
)