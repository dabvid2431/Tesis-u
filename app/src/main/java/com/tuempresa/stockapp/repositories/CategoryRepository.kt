package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Category
import retrofit2.Call

class CategoryRepository : ICategoryRepository {
    private val api get() = RetrofitClient.instance

    override fun getCategories(): Call<List<Category>> = api.getCategories()
    override fun createCategory(category: Category): Call<Category> = api.createCategory(category)
    override fun updateCategory(id: Int, category: Category): Call<Category> = api.updateCategory(id, category)
    override fun deleteCategory(id: Int): Call<Unit> = api.deleteCategory(id)
}
