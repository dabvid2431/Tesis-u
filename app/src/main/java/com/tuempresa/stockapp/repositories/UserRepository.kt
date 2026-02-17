package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.User
import retrofit2.Call

class UserRepository : IUserRepository {
    private val api get() = RetrofitClient.instance

    override fun login(username: String, password: String): Call<User> = api.login(mapOf("username" to username, "password" to password))
    override fun createUser(user: User): Call<User> = api.createUser(user)
    override fun createUserMap(userMap: Map<String, String>): Call<User> = api.createUserMap(userMap)
}
