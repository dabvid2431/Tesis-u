package com.tuempresa.stockapp.models

data class Client(
    val id: Int,
    val name: String,
    val phone: String?,
    val email: String?,
    val address: String?
)