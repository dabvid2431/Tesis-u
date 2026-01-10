package com.tuempresa.stockapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.Category
import com.tuempresa.stockapp.repositories.CategoryRepository
import com.tuempresa.stockapp.repositories.ICategoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(private val repository: ICategoryRepository = CategoryRepository()) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()

    val categories: LiveData<List<Category>> get() = _categories

    fun fetchCategories() {
        repository.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                _categories.value = response.body()
            }
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                _categories.value = emptyList()
            }
        })
    }

        fun createCategory(category: Category, onResult: (Category?) -> Unit) {
            repository.createCategory(category).enqueue(object : Callback<Category> {
                override fun onResponse(call: Call<Category>, response: Response<Category>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Category>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun updateCategory(id: Int, category: Category, onResult: (Category?) -> Unit) {
            repository.updateCategory(id, category).enqueue(object : Callback<Category> {
                override fun onResponse(call: Call<Category>, response: Response<Category>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Category>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun deleteCategory(id: Int, onResult: (Boolean) -> Unit) {
            repository.deleteCategory(id).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    onResult(response.isSuccessful)
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onResult(false)
                }
            })
        }
}
