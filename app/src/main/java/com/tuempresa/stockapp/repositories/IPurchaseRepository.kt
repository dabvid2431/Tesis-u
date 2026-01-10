package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.Purchase
import retrofit2.Call

interface IPurchaseRepository {
    fun getPurchases(): Call<List<Purchase>>
    fun createPurchase(purchase: Purchase): Call<Purchase>
    fun createPurchaseMap(purchaseMap: Map<String, Any>): Call<Purchase>
    fun updatePurchase(id: Int, purchase: Purchase): Call<Purchase>
    fun deletePurchase(id: Int): Call<Unit>
}