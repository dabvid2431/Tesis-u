package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Supplier
import retrofit2.Call

class SupplierRepository : ISupplierRepository {
    private val api = RetrofitClient.instance

    override fun getSuppliers(): Call<List<Supplier>> = api.getSuppliers()
    override fun createSupplier(supplier: Supplier): Call<Supplier> = api.createSupplier(supplier)
    override fun updateSupplier(id: Int, supplier: Supplier): Call<Supplier> = api.updateSupplier(id, supplier)
    override fun deleteSupplier(id: Int): Call<Unit> = api.deleteSupplier(id)
}
