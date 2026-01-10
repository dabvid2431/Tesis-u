package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.Category
import retrofit2.Call

interface ICategoryRepository {
    fun getCategories(): Call<List<Category>>
    fun createCategory(category: Category): Call<Category>
    fun updateCategory(id: Int, category: Category): Call<Category>
    fun deleteCategory(id: Int): Call<Unit>
}
