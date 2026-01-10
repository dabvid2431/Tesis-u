package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Sale
import retrofit2.Call

class SaleRepository : ISaleRepository {
    private val api = RetrofitClient.instance

    override fun getSales(): Call<List<Sale>> = api.getSales()
    override fun createSale(sale: Sale): Call<Sale> = api.createSale(sale)
    override fun createSaleMap(saleMap: Map<String, Any>): Call<Sale> = api.createSaleMap(saleMap)
    override fun updateSale(id: Int, sale: Sale): Call<Sale> = api.updateSale(id, sale)
    override fun deleteSale(id: Int): Call<Unit> = api.deleteSale(id)
}
