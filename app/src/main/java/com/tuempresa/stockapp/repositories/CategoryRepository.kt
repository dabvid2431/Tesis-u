package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Category
import retrofit2.Call

class CategoryRepository {
    private val api = RetrofitClient.instance

    fun getCategories(): Call<List<Category>> = api.getCategories()
    fun createCategory(category: Category): Call<Category> = api.createCategory(category)
    fun updateCategory(id: Int, category: Category): Call<Category> = api.updateCategory(id, category)
    fun deleteCategory(id: Int): Call<Void> = api.deleteCategory(id)
}
