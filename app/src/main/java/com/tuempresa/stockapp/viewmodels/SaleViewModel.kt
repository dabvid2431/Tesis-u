package com.tuempresa.stockapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.Sale
import com.tuempresa.stockapp.repositories.SaleRepository
import com.tuempresa.stockapp.repositories.ISaleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SaleViewModel(private val repository: ISaleRepository = SaleRepository()) : ViewModel() {
    private val _sales = MutableLiveData<List<Sale>>()
    val sales: LiveData<List<Sale>> get() = _sales

    fun fetchSales() {
        repository.getSales().enqueue(object : Callback<List<Sale>> {
            override fun onResponse(call: Call<List<Sale>>, response: Response<List<Sale>>) {
                _sales.value = response.body()
            }
            override fun onFailure(call: Call<List<Sale>>, t: Throwable) {
                _sales.value = emptyList()
            }
        })
    }
    fun createSale(sale: Sale, onResult: (Sale?) -> Unit) {
        repository.createSale(sale).enqueue(object : Callback<Sale> {
            override fun onResponse(call: Call<Sale>, response: Response<Sale>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<Sale>, t: Throwable) {
                onResult(null)
            }
        })
    }

    // Create sale using a flexible map payload (clientId + items list) to match backend
    fun createSaleMap(saleMap: Map<String, Any>, onResult: (Sale?) -> Unit) {
        repository.createSaleMap(saleMap).enqueue(object : Callback<Sale> {
            override fun onResponse(call: Call<Sale>, response: Response<Sale>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<Sale>, t: Throwable) {
                onResult(null)
            }
        })
    }

        fun updateSale(id: Int, sale: Sale, onResult: (Sale?) -> Unit) {
            repository.updateSale(id, sale).enqueue(object : Callback<Sale> {
                override fun onResponse(call: Call<Sale>, response: Response<Sale>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Sale>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun deleteSale(id: Int, onResult: (Boolean) -> Unit) {
            repository.deleteSale(id).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    onResult(response.isSuccessful)
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onResult(false)
                }
            })
        }
}
