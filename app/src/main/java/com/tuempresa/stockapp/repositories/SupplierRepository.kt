package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Supplier
import retrofit2.Call

class SupplierRepository {
    private val api = RetrofitClient.instance

    fun getSuppliers(): Call<List<Supplier>> = api.getSuppliers()
    fun createSupplier(supplier: Supplier): Call<Supplier> = api.createSupplier(supplier)
    fun updateSupplier(id: Int, supplier: Supplier): Call<Supplier> = api.updateSupplier(id, supplier)
    fun deleteSupplier(id: Int): Call<Unit> = api.deleteSupplier(id)
}
