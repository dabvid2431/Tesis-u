package com.tuempresa.stockapp.models

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val role: String // "admin" o "vendedor"
)
