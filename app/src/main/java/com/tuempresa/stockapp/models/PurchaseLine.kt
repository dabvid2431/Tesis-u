package com.tuempresa.stockapp.models

data class PurchaseLine(
    var productId: Int = -1,
    var productName: String? = null,
    var quantity: Int = 1,
    var price: Double = 0.0
) {
    val subtotal: Double
        get() = quantity * price
}
