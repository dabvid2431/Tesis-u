package com.tuempresa.stockapp.models

data class Sale(
    val id: Int,
    val clientId: Int,
    val userId: Int? = null,
    val sellerName: String? = null,
    val date: String?,
    val total: Double
)