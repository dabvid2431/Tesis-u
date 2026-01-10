package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.User
import retrofit2.Call

interface IUserRepository {
    fun login(username: String, password: String): Call<User>
    fun createUser(user: User): Call<User>
    fun createUserMap(userMap: Map<String, String>): Call<User>
}
