package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.Sale
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class SaleViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // retrofit-mock helpers
    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun fetchSales_setsLiveDataOnSuccess() {
        val expected = listOf(Sale(id = 1, clientId = 10, date = "2026-01-01", total = 100.0))
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = fakeCallSuccess<List<Sale>>(Response.success(expected))
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        vm.fetchSales()
        val value = vm.sales.value
        assertEquals(expected, value)
    }

    @Test
    fun fetchSales_onFailure_setsEmptyList() {
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = fakeCallFailure<List<Sale>>(RuntimeException("network"))
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        vm.fetchSales()
        val value = vm.sales.value
        assertTrue(value!!.isEmpty())
    }

    @Test
    fun createSale_onSuccess_callsOnResult() {
        val newSale = Sale(id = 2, clientId = 11, date = "2026-01-02", total = 200.0)
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = fakeCallSuccess<Sale>(Response.success(newSale))
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        var result: Sale? = null
        vm.createSale(newSale) { res -> result = res }
        assertEquals(newSale, result)
    }

    @Test
    fun createSale_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = fakeCallFailure<Sale>(RuntimeException("network"))
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        var result: Sale? = Sale(id = -1, clientId = -1, date = "", total = 0.0)
        vm.createSale(Sale(id = 0, clientId = 0, date = "", total = 0.0)) { res -> result = res }
        assertNull(result)
    }

    @Test
    fun createSaleMap_onSuccess_callsOnResult() {
        val newSale = Sale(id = 3, clientId = 12, date = "2026-01-03", total = 300.0)
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = fakeCallSuccess<Sale>(Response.success(newSale))
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        var result: Sale? = null
        vm.createSaleMap(mapOf("clientId" to 12)) { res -> result = res }
        assertEquals(newSale, result)
    }

    @Test
    fun createSaleMap_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = fakeCallFailure<Sale>(RuntimeException("network"))
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        var result: Sale? = Sale(id = -1, clientId = -1, date = "", total = 0.0)
        vm.createSaleMap(mapOf("clientId" to 0)) { res -> result = res }
        assertNull(result)
    }

    @Test
    fun updateSale_onSuccess_callsOnResult() {
        val updatedSale = Sale(id = 4, clientId = 13, date = "2026-01-04", total = 400.0)
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = fakeCallSuccess<Sale>(Response.success(updatedSale))
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        var result: Sale? = null
        vm.updateSale(4, updatedSale) { res -> result = res }
        assertEquals(updatedSale, result)
    }

    @Test
    fun updateSale_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = fakeCallFailure<Sale>(RuntimeException("network"))
            override fun deleteSale(id: Int) = throw UnsupportedOperationException()
        }

        val vm = SaleViewModel(repo)
        var result: Sale? = Sale(id = -1, clientId = -1, date = "", total = 0.0)
        vm.updateSale(0, Sale(id = 0, clientId = 0, date = "", total = 0.0)) { res -> result = res }
        assertNull(result)
    }

    @Test
    fun deleteSale_onSuccess_callsOnResultTrue() {
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = fakeCallSuccess<Unit>(Response.success(Unit))
        }

        val vm = SaleViewModel(repo)
        var result = false
        vm.deleteSale(1) { res -> result = res }
        assertTrue(result)
    }

    @Test
    fun deleteSale_onFailure_callsOnResultFalse() {
        val repo = object : com.tuempresa.stockapp.repositories.ISaleRepository {
            override fun getSales() = throw UnsupportedOperationException()
            override fun createSale(sale: Sale) = throw UnsupportedOperationException()
            override fun createSaleMap(saleMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateSale(id: Int, sale: Sale) = throw UnsupportedOperationException()
            override fun deleteSale(id: Int) = fakeCallFailure<Unit>(RuntimeException("network"))
        }

        val vm = SaleViewModel(repo)
        var result = true
        vm.deleteSale(1) { res -> result = res }
        assertFalse(result)
    }
}