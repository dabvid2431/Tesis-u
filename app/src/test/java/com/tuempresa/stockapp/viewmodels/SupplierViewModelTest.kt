package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.Supplier
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class SupplierViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // retrofit-mock helpers
    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun fetchSuppliers_setsLiveDataOnSuccess() {
        val expected = listOf(Supplier(id = 1, name = "Proveedor A", contact = "Juan", address = "Calle 1"))
        val repo = object : com.tuempresa.stockapp.repositories.ISupplierRepository {
            override fun getSuppliers() = fakeCallSuccess<List<Supplier>>(Response.success(expected))
            override fun createSupplier(supplier: Supplier) = throw UnsupportedOperationException()
            override fun updateSupplier(id: Int, supplier: Supplier) = throw UnsupportedOperationException()
            override fun deleteSupplier(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SupplierViewModel(repo)
        vm.fetchSuppliers()
        val value = vm.suppliers.value
        assertEquals(expected, value)
    }

    @Test
    fun fetchSuppliers_onFailure_setsEmptyList() {
        val repo = object : com.tuempresa.stockapp.repositories.ISupplierRepository {
            override fun getSuppliers() = fakeCallFailure<List<Supplier>>(RuntimeException("network"))
            override fun createSupplier(supplier: Supplier) = throw UnsupportedOperationException()
            override fun updateSupplier(id: Int, supplier: Supplier) = throw UnsupportedOperationException()
            override fun deleteSupplier(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SupplierViewModel(repo)
        vm.fetchSuppliers()
        val value = vm.suppliers.value
        assertTrue(value!!.isEmpty())
    }

    @Test
    fun createSupplier_onSuccess_callsOnResult() {
        val newSup = Supplier(id = 2, name = "Proveedor B", contact = "Ana", address = "Av 2")
        val repo = object : com.tuempresa.stockapp.repositories.ISupplierRepository {
            override fun getSuppliers() = throw UnsupportedOperationException()
            override fun createSupplier(supplier: Supplier) = fakeCallSuccess<Supplier>(Response.success(newSup))
            override fun updateSupplier(id: Int, supplier: Supplier) = throw UnsupportedOperationException()
            override fun deleteSupplier(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SupplierViewModel(repo)
        var result: Supplier? = null
        vm.createSupplier(newSup) { res -> result = res }
        assertEquals(newSup, result)
    }

    @Test
    fun createSupplier_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.ISupplierRepository {
            override fun getSuppliers() = throw UnsupportedOperationException()
            override fun createSupplier(supplier: Supplier) = fakeCallFailure<Supplier>(RuntimeException("network"))
            override fun updateSupplier(id: Int, supplier: Supplier) = throw UnsupportedOperationException()
            override fun deleteSupplier(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SupplierViewModel(repo)
        var result: Supplier? = Supplier(id = -1, name = "", contact = "", address = "")
        vm.createSupplier(Supplier(id = 0, name = "X", contact = "", address = "")) { res -> result = res }
        assertNull(result)
    }
}
