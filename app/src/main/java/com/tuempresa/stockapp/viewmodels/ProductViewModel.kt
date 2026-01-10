package com.tuempresa.stockapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.repositories.ProductRepository
import com.tuempresa.stockapp.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel(private val repository: com.tuempresa.stockapp.repositories.IProductRepository = ProductRepository()) : ViewModel() {
    fun createProductMap(productMap: Map<String, Any>, onResult: (Product?) -> Unit) {
        repository.createProductMap(productMap).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                onResult(null)
            }
        })
    }

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> get() = _products

    fun fetchProducts() {
        _products.value = Resource.Loading()
        repository.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                val productList = response.body()
                if (response.isSuccessful && productList != null) {
                    _products.value = Resource.Success(productList)
                } else {
                    val err = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    try { Log.e("ProductVM", "fetchProducts failed code=${response.code()} error=$err") } catch (_: Throwable) {}
                    _products.value = Resource.Error("Error al obtener productos (code=${response.code()})")
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                try { Log.e("ProductVM", "fetchProducts network failure: ${t.message}", t) } catch (_: Throwable) {}
                _products.value = Resource.Error("Error de red: ${t.message}")
            }
        })
    }

    fun createProduct(product: Product, onResult: (Product?) -> Unit) {
        repository.createProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun updateProduct(id: Int, product: Product, onResult: (Product?) -> Unit) {
        repository.updateProduct(id, product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun deleteProduct(id: Int, onResult: (Boolean) -> Unit) {
        repository.deleteProduct(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onResult(response.isSuccessful)
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onResult(false)
            }
        })
    }
}
