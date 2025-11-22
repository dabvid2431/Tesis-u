package com.tuempresa.stockapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.Supplier
import com.tuempresa.stockapp.repositories.SupplierRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupplierViewModel : ViewModel() {
    private val repository = SupplierRepository()
    private val _suppliers = MutableLiveData<List<Supplier>>()
    val suppliers: LiveData<List<Supplier>> get() = _suppliers

    fun fetchSuppliers() {
        repository.getSuppliers().enqueue(object : Callback<List<Supplier>> {
            override fun onResponse(call: Call<List<Supplier>>, response: Response<List<Supplier>>) {
                _suppliers.value = response.body()
            }
            override fun onFailure(call: Call<List<Supplier>>, t: Throwable) {
                _suppliers.value = emptyList()
            }
        })
    }

        fun createSupplier(supplier: Supplier, onResult: (Supplier?) -> Unit) {
            repository.createSupplier(supplier).enqueue(object : Callback<Supplier> {
                override fun onResponse(call: Call<Supplier>, response: Response<Supplier>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun updateSupplier(id: Int, supplier: Supplier, onResult: (Supplier?) -> Unit) {
            repository.updateSupplier(id, supplier).enqueue(object : Callback<Supplier> {
                override fun onResponse(call: Call<Supplier>, response: Response<Supplier>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun deleteSupplier(id: Int, onResult: (Boolean) -> Unit) {
            repository.deleteSupplier(id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResult(response.isSuccessful)
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onResult(false)
                }
            })
        }
}
