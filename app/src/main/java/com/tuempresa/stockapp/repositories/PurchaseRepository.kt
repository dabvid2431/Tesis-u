package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Purchase
import retrofit2.Call

class PurchaseRepository : IPurchaseRepository {
    private val api get() = RetrofitClient.instance

    override fun getPurchases(): Call<List<Purchase>> = api.getPurchases()
    override fun createPurchase(purchase: Purchase): Call<Purchase> = api.createPurchase(purchase)
    override fun createPurchaseMap(purchaseMap: Map<String, Any>): Call<Purchase> = api.createPurchaseMap(purchaseMap)
    override fun updatePurchase(id: Int, purchase: Purchase): Call<Purchase> = api.updatePurchase(id, purchase)
    override fun deletePurchase(id: Int): Call<Unit> = api.deletePurchase(id)
}
