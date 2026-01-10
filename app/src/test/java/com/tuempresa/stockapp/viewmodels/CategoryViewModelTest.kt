package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.Category
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class CategoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // retrofit-mock helpers
    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun fetchCategories_setsLiveDataOnSuccess() {
        val expected = listOf(Category(id = 1, name = "Papelería", description = "Varios útiles"))
        val repo = object : com.tuempresa.stockapp.repositories.ICategoryRepository {
            override fun getCategories() = fakeCallSuccess<List<Category>>(Response.success(expected))
            override fun createCategory(category: Category) = throw UnsupportedOperationException()
            override fun updateCategory(id: Int, category: Category) = throw UnsupportedOperationException()
            override fun deleteCategory(id: Int) = throw UnsupportedOperationException()
        }

        val vm = CategoryViewModel(repo)
        vm.fetchCategories()
        val value = vm.categories.value
        assertEquals(expected, value)
    }

    @Test
    fun fetchCategories_onFailure_setsEmptyList() {
        val repo = object : com.tuempresa.stockapp.repositories.ICategoryRepository {
            override fun getCategories() = fakeCallFailure<List<Category>>(RuntimeException("network"))
            override fun createCategory(category: Category) = throw UnsupportedOperationException()
            override fun updateCategory(id: Int, category: Category) = throw UnsupportedOperationException()
            override fun deleteCategory(id: Int) = throw UnsupportedOperationException()
        }

        val vm = CategoryViewModel(repo)
        vm.fetchCategories()
        val value = vm.categories.value
        assertTrue(value!!.isEmpty())
    }

    @Test
    fun createCategory_onSuccess_callsOnResult() {
        val newCat = Category(id = 2, name = "Limpieza", description = "Productos de limpieza")
        val repo = object : com.tuempresa.stockapp.repositories.ICategoryRepository {
            override fun getCategories() = throw UnsupportedOperationException()
            override fun createCategory(category: Category) = fakeCallSuccess<Category>(Response.success(newCat))
            override fun updateCategory(id: Int, category: Category) = throw UnsupportedOperationException()
            override fun deleteCategory(id: Int) = throw UnsupportedOperationException()
        }

        val vm = CategoryViewModel(repo)
        var result: Category? = null
        vm.createCategory(newCat) { res -> result = res }
        assertEquals(newCat, result)
    }

    @Test
    fun createCategory_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.ICategoryRepository {
            override fun getCategories() = throw UnsupportedOperationException()
            override fun createCategory(category: Category) = fakeCallFailure<Category>(RuntimeException("network"))
            override fun updateCategory(id: Int, category: Category) = throw UnsupportedOperationException()
            override fun deleteCategory(id: Int) = throw UnsupportedOperationException()
        }

        val vm = CategoryViewModel(repo)
        var result: Category? = Category(id = -1, name = "", description = "")
        vm.createCategory(Category(id = 0, name = "X", description = "")) { res -> result = res }
        assertNull(result)
    }
}
