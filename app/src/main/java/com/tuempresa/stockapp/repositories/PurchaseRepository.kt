package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Purchase
import retrofit2.Call

class PurchaseRepository {
    private val api = RetrofitClient.instance

    fun getPurchases(): Call<List<Purchase>> = api.getPurchases()
    fun createPurchase(purchase: Purchase): Call<Purchase> = api.createPurchase(purchase)
    fun createPurchaseMap(purchaseMap: Map<String, Any>): Call<Purchase> = api.createPurchaseMap(purchaseMap)
    fun updatePurchase(id: Int, purchase: Purchase): Call<Purchase> = api.updatePurchase(id, purchase)
    fun deletePurchase(id: Int): Call<Unit> = api.deletePurchase(id)
}
