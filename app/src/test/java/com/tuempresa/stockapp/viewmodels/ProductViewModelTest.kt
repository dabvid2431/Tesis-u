package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.utils.Resource
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class ProductViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // retrofit-mock helpers
    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun fetchProducts_setsLiveDataOnSuccess() {
        val expected = listOf(
            Product(id = 1, name = "Foo", description = "", categoryId = 0, supplierId = 0, purchasePrice = 1.0, salePrice = 9.0, stock = 5)
        )
        val repo = object : com.tuempresa.stockapp.repositories.IProductRepository {
            override fun getProducts() = fakeCallSuccess<List<Product>>(Response.success(expected))
            override fun createProduct(product: Product) = throw UnsupportedOperationException()
            override fun createProductMap(productMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateProduct(id: Int, product: Product) = throw UnsupportedOperationException()
            override fun deleteProduct(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ProductViewModel(repo)
        vm.fetchProducts()
        val value = vm.products.value
        assertTrue(value is Resource.Success)
        assertEquals(expected, (value as Resource.Success).data)
    }

    @Test
    fun fetchProducts_onFailure_setsError() {
        val repo = object : com.tuempresa.stockapp.repositories.IProductRepository {
            override fun getProducts() = fakeCallFailure<List<Product>>(RuntimeException("network"))
            override fun createProduct(product: Product) = throw UnsupportedOperationException()
            override fun createProductMap(productMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateProduct(id: Int, product: Product) = throw UnsupportedOperationException()
            override fun deleteProduct(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ProductViewModel(repo)
        vm.fetchProducts()
        val value = vm.products.value
        assertTrue(value is Resource.Error)
        assertTrue((value as Resource.Error).message!!.contains("Error de red"))
    }

    @Test
    fun deleteProduct_onSuccess_callsOnResultTrue() {
        val repo = object : com.tuempresa.stockapp.repositories.IProductRepository {
            override fun getProducts() = throw UnsupportedOperationException()
            override fun createProduct(product: Product) = throw UnsupportedOperationException()
            override fun createProductMap(productMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateProduct(id: Int, product: Product) = throw UnsupportedOperationException()
            override fun deleteProduct(id: Int) = fakeCallSuccess<Unit>(Response.success(Unit))
        }

        val vm = ProductViewModel(repo)
        var result: Boolean? = null
        vm.deleteProduct(12) { res -> result = res }
        assertEquals(true, result)
    }

    @Test
    fun deleteProduct_onFailure_callsOnResultFalse() {
        val repo = object : com.tuempresa.stockapp.repositories.IProductRepository {
            override fun getProducts() = throw UnsupportedOperationException()
            override fun createProduct(product: Product) = throw UnsupportedOperationException()
            override fun createProductMap(productMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateProduct(id: Int, product: Product) = throw UnsupportedOperationException()
            override fun deleteProduct(id: Int) = fakeCallFailure<Unit>(RuntimeException("network"))
        }

        val vm = ProductViewModel(repo)
        var result: Boolean? = null
        vm.deleteProduct(12) { res -> result = res }
        assertEquals(false, result)
    }
}
