package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.Purchase
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Use retrofit-mock helper Calls to avoid implementing Call<T> manually
    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun fetchPurchases_setsLiveDataOnSuccess() {
        val expected = listOf(Purchase(id = 1, supplierId = 2, date = null, total = 100.0, items = emptyList()))
        val repo = object : com.tuempresa.stockapp.repositories.IPurchaseRepository {
            override fun getPurchases() = fakeCallSuccess(Response.success(expected))
            override fun createPurchase(purchase: Purchase) = throw UnsupportedOperationException()
            override fun createPurchaseMap(purchaseMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updatePurchase(id: Int, purchase: Purchase) = throw UnsupportedOperationException()
            override fun deletePurchase(id: Int) = throw UnsupportedOperationException()
        }

        val vm = PurchaseViewModel(repo)
        vm.fetchPurchases()
        val actual = vm.purchases.value
        assertEquals(expected, actual)
    }

    @Test
    fun deletePurchase_onSuccess_callsOnResultTrue() {
        val repo = object : com.tuempresa.stockapp.repositories.IPurchaseRepository {
            override fun getPurchases() = throw UnsupportedOperationException()
            override fun createPurchase(purchase: Purchase) = throw UnsupportedOperationException()
            override fun createPurchaseMap(purchaseMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updatePurchase(id: Int, purchase: Purchase) = throw UnsupportedOperationException()
            override fun deletePurchase(id: Int) = fakeCallSuccess<Unit>(Response.success(Unit))
        }

        val vm = PurchaseViewModel(repo)
        var result: Boolean? = null
        vm.deletePurchase(123) { res -> result = res }
        assertEquals(true, result)
    }

    @Test
    fun deletePurchase_onFailure_callsOnResultFalse() {
        val repo = object : com.tuempresa.stockapp.repositories.IPurchaseRepository {
            override fun getPurchases() = throw UnsupportedOperationException()
            override fun createPurchase(purchase: Purchase) = throw UnsupportedOperationException()
            override fun createPurchaseMap(purchaseMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updatePurchase(id: Int, purchase: Purchase) = throw UnsupportedOperationException()
            override fun deletePurchase(id: Int) = fakeCallFailure<Unit>(RuntimeException("network error"))
        }

        val vm = PurchaseViewModel(repo)
        var result: Boolean? = null
        vm.deletePurchase(123) { res -> result = res }
        assertEquals(false, result)
    }
}