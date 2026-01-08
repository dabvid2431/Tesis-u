package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Sale
import retrofit2.Call

class SaleRepository {
    private val api = RetrofitClient.instance

    fun getSales(): Call<List<Sale>> = api.getSales()
    fun createSale(sale: Sale): Call<Sale> = api.createSale(sale)
    fun createSaleMap(saleMap: Map<String, Any>): Call<Sale> = api.createSaleMap(saleMap)
    fun updateSale(id: Int, sale: Sale): Call<Sale> = api.updateSale(id, sale)
    fun deleteSale(id: Int): Call<Unit> = api.deleteSale(id)
}
