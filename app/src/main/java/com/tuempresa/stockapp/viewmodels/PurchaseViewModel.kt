package com.tuempresa.stockapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.Purchase
import com.tuempresa.stockapp.repositories.PurchaseRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseViewModel : ViewModel() {
    private val repository = PurchaseRepository()
    private val _purchases = MutableLiveData<List<Purchase>>()
    val purchases: LiveData<List<Purchase>> get() = _purchases

    fun fetchPurchases() {
        repository.getPurchases().enqueue(object : Callback<List<Purchase>> {
            override fun onResponse(call: Call<List<Purchase>>, response: Response<List<Purchase>>) {
                _purchases.value = response.body()
            }
            override fun onFailure(call: Call<List<Purchase>>, t: Throwable) {
                _purchases.value = emptyList()
            }
        })
    }

        fun createPurchase(purchase: Purchase, onResult: (Purchase?) -> Unit) {
            repository.createPurchase(purchase).enqueue(object : Callback<Purchase> {
                override fun onResponse(call: Call<Purchase>, response: Response<Purchase>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Purchase>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        // Create purchase using a flexible map payload (supplierId + items list) to match backend
        fun createPurchaseMap(purchaseMap: Map<String, Any>, onResult: (Purchase?) -> Unit) {
            repository.createPurchaseMap(purchaseMap).enqueue(object : Callback<Purchase> {
                override fun onResponse(call: Call<Purchase>, response: Response<Purchase>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Purchase>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun updatePurchase(id: Int, purchase: Purchase, onResult: (Purchase?) -> Unit) {
            repository.updatePurchase(id, purchase).enqueue(object : Callback<Purchase> {
                override fun onResponse(call: Call<Purchase>, response: Response<Purchase>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Purchase>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun deletePurchase(id: Int, onResult: (Boolean) -> Unit) {
            repository.deletePurchase(id).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Log.d("PurchaseVM", "deletePurchase success id=$id code=${response.code()}")
                        onResult(true)
                    } else {
                        val errBody = try { response.errorBody()?.string() } catch (e: Exception) { null }
                        Log.e("PurchaseVM", "deletePurchase failed id=$id code=${response.code()} error=$errBody")
                        onResult(false)
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("PurchaseVM", "deletePurchase network failure id=$id: ${t.message}", t)
                    onResult(false)
                }
            })
        }
}
