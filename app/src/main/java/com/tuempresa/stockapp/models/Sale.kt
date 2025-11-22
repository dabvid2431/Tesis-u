package com.tuempresa.stockapp.models

data class Sale(
    val id: Int,
    val clientId: Int,
    val date: String?,
    val total: Double
)