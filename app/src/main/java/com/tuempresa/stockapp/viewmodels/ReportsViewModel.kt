package com.tuempresa.stockapp.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tuempresa.stockapp.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    
    val salesReportData = MutableLiveData<Map<String, Any>>()
    val topProductsData = MutableLiveData<List<Map<String, Any>>>()
    val lowStockData = MutableLiveData<List<Map<String, Any>>>()
    val stockMovementsData = MutableLiveData<List<Map<String, Any>>>()
    
    private val apiService get() = RetrofitClient.instance
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    fun fetchSalesReport() {
        apiService.getSalesReport().enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ReportsVM", "Sales Report: ${response.body()}")
                    salesReportData.value = response.body()!!
                }
            }
            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("ReportsVM", "Sales report error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
    
    fun fetchTopProducts() {
        apiService.getTopProducts().enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(call: Call<List<Map<String, Any>>>, response: Response<List<Map<String, Any>>>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ReportsVM", "Top Products: ${response.body()?.size} items - ${response.body()}")
                    topProductsData.value = response.body()!!
                }
            }
            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                Log.e("ReportsVM", "Top products error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
    
    fun fetchLowStock() {
        apiService.getLowStock().enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(call: Call<List<Map<String, Any>>>, response: Response<List<Map<String, Any>>>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ReportsVM", "Low Stock: ${response.body()?.size} items - ${response.body()}")
                    lowStockData.value = response.body()!!
                }
            }
            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                Log.e("ReportsVM", "Low stock error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
    
    fun fetchStockMovements() {
        apiService.getStockMovements().enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(call: Call<List<Map<String, Any>>>, response: Response<List<Map<String, Any>>>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ReportsVM", "Stock Movements: ${response.body()?.size} items - ${response.body()}")
                    stockMovementsData.value = response.body()!!
                }
            }
            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                Log.e("ReportsVM", "Stock movements error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}
