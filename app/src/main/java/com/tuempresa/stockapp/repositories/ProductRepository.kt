package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Product
import retrofit2.Call

class ProductRepository {
    private val api = RetrofitClient.instance

    fun getProducts(): Call<List<Product>> = api.getProducts()
    fun createProduct(product: Product): Call<Product> = api.createProduct(product)
    fun createProductMap(productMap: Map<String, Any>): Call<Product> = api.createProductMap(productMap)
    fun updateProduct(id: Int, product: Product): Call<Product> = api.updateProduct(id, product)
    fun deleteProduct(id: Int): Call<Unit> = api.deleteProduct(id)
}
