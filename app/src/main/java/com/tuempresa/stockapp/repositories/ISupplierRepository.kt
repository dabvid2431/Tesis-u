package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.Supplier
import retrofit2.Call

interface ISupplierRepository {
    fun getSuppliers(): Call<List<Supplier>>
    fun createSupplier(supplier: Supplier): Call<Supplier>
    fun updateSupplier(id: Int, supplier: Supplier): Call<Supplier>
    fun deleteSupplier(id: Int): Call<Unit>
}
