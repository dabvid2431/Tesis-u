package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Product
import retrofit2.Call

class ProductRepository : IProductRepository {
    private val api get() = RetrofitClient.instance

    override fun getProducts(): Call<List<Product>> = api.getProducts()
    override fun createProduct(product: Product): Call<Product> = api.createProduct(product)
    override fun createProductMap(productMap: Map<String, Any>): Call<Product> = api.createProductMap(productMap)
    override fun updateProduct(id: Int, product: Product): Call<Product> = api.updateProduct(id, product)
    override fun deleteProduct(id: Int): Call<Unit> = api.deleteProduct(id)
}
