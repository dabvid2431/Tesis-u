package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.Sale
import retrofit2.Call

interface ISaleRepository {
    fun getSales(): Call<List<Sale>>
    fun createSale(sale: Sale): Call<Sale>
    fun createSaleMap(saleMap: Map<String, Any>): Call<Sale>
    fun updateSale(id: Int, sale: Sale): Call<Sale>
    fun deleteSale(id: Int): Call<Unit>
}