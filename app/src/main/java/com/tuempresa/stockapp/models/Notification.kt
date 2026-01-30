package com.tuempresa.stockapp.models

data class Notification(
    val id: Int,
    val message: String,
    val type: String,
    val read: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
