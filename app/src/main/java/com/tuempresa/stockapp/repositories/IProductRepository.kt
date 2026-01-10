package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.Product
import retrofit2.Call

interface IProductRepository {
    fun getProducts(): Call<List<Product>>
    fun createProduct(product: Product): Call<Product>
    fun createProductMap(productMap: Map<String, Any>): Call<Product>
    fun updateProduct(id: Int, product: Product): Call<Product>
    fun deleteProduct(id: Int): Call<Unit>
}
