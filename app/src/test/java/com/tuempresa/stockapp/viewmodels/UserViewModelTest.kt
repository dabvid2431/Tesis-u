package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.User
import com.tuempresa.stockapp.utils.Resource
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class UserViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun login_setsSuccessOnValidCredentials() {
        val expected = User(id = 1, username = "u", password = "p", role = "admin")
        val repo = object : com.tuempresa.stockapp.repositories.IUserRepository {
            override fun login(username: String, password: String) = fakeCallSuccess(Response.success(expected))
            override fun createUser(user: User) = throw UnsupportedOperationException()
            override fun createUserMap(userMap: Map<String, String>) = throw UnsupportedOperationException()
        }

        val vm = UserViewModel(repo)
        vm.login("u", "p")
        val value = vm.user.value
        assertTrue(value is Resource.Success)
        assertEquals(expected, (value as Resource.Success).data)
    }

    @Test
    fun login_setsErrorOnFailure() {
        val repo = object : com.tuempresa.stockapp.repositories.IUserRepository {
            override fun login(username: String, password: String) = fakeCallFailure<User>(RuntimeException("network"))
            override fun createUser(user: User) = throw UnsupportedOperationException()
            override fun createUserMap(userMap: Map<String, String>) = throw UnsupportedOperationException()
        }

        val vm = UserViewModel(repo)
        vm.login("u", "p")
        val value = vm.user.value
        assertTrue(value is Resource.Error)
        assertTrue((value as Resource.Error).message!!.contains("Error de red"))
    }

    @Test
    fun createUser_setsSuccessOnResponse() {
        val expected = User(id = 2, username = "x", password = "y", role = "vendedor")
        val repo = object : com.tuempresa.stockapp.repositories.IUserRepository {
            override fun login(username: String, password: String) = throw UnsupportedOperationException()
            override fun createUser(user: User) = fakeCallSuccess(Response.success(expected))
            override fun createUserMap(userMap: Map<String, String>) = throw UnsupportedOperationException()
        }

        val vm = UserViewModel(repo)
        vm.createUser(expected)
        val value = vm.user.value
        assertTrue(value is Resource.Success)
        assertEquals(expected, (value as Resource.Success).data)
    }

    @Test
    fun createUser_setsErrorOnNetworkFailure() {
        val repo = object : com.tuempresa.stockapp.repositories.IUserRepository {
            override fun login(username: String, password: String) = throw UnsupportedOperationException()
            override fun createUser(user: User) = fakeCallFailure<User>(RuntimeException("network"))
            override fun createUserMap(userMap: Map<String, String>) = throw UnsupportedOperationException()
        }

        val vm = UserViewModel(repo)
        vm.createUser(User(id = 0, username = "", password = "", role = ""))
        val value = vm.user.value
        assertTrue(value is Resource.Error)
        assertTrue((value as Resource.Error).message!!.contains("Error de red"))
    }

    @Test
    fun createUserMap_setsSuccessOnResponse() {
        val expected = User(id = 3, username = "mapu", password = "", role = "vendedor")
        val repo = object : com.tuempresa.stockapp.repositories.IUserRepository {
            override fun login(username: String, password: String) = throw UnsupportedOperationException()
            override fun createUser(user: User) = throw UnsupportedOperationException()
            override fun createUserMap(userMap: Map<String, String>) = fakeCallSuccess(Response.success(expected))
        }

        val vm = UserViewModel(repo)
        vm.createUserMap(mapOf("username" to "mapu"))
        val value = vm.user.value
        assertTrue(value is Resource.Success)
        assertEquals(expected, (value as Resource.Success).data)
    }

    @Test
    fun createUserMap_setsErrorOnFailure() {
        val repo = object : com.tuempresa.stockapp.repositories.IUserRepository {
            override fun login(username: String, password: String) = throw UnsupportedOperationException()
            override fun createUser(user: User) = throw UnsupportedOperationException()
            override fun createUserMap(userMap: Map<String, String>) = fakeCallFailure<User>(RuntimeException("network"))
        }

        val vm = UserViewModel(repo)
        vm.createUserMap(mapOf("username" to "x"))
        val value = vm.user.value
        assertTrue(value is Resource.Error)
        assertTrue((value as Resource.Error).message!!.contains("Error de red"))
    }
}
