package com.tuempresa.stockapp.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val categoryId: Int,
    val supplierId: Int,
    val purchasePrice: Double,
    val salePrice: Double,
    val stock: Int
) {
    // Keep a convenient alias `price` used in the UI (maps to salePrice)
    val price: Double
        get() = salePrice
}
