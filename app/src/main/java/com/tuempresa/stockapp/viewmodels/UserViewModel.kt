package com.tuempresa.stockapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.User
import com.tuempresa.stockapp.repositories.UserRepository
import com.tuempresa.stockapp.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    fun createUserMap(userMap: Map<String, String>) {
        _user.value = Resource.Loading()
        repository.createUserMap(userMap).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val userObj = response.body()
                if (response.isSuccessful && userObj != null) {
                    _user.value = Resource.Success(userObj)
                } else {
                    _user.value = Resource.Error("Error al crear la cuenta")
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                _user.value = Resource.Error("Error de red: ${t.message}")
            }
        })
    }
    private val repository = UserRepository()
    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> get() = _user

    fun login(username: String, password: String) {
        _user.value = Resource.Loading()
        repository.login(username, password).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val userObj = response.body()
                if (response.isSuccessful && userObj != null) {
                    _user.value = Resource.Success(userObj)
                } else {
                    _user.value = Resource.Error("Credenciales incorrectas")
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                _user.value = Resource.Error("Error de red: ${t.message}")
            }
        })
    }

    fun createUser(user: User) {
        _user.value = Resource.Loading()
        repository.createUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val userObj = response.body()
                if (response.isSuccessful && userObj != null) {
                    _user.value = Resource.Success(userObj)
                } else {
                    _user.value = Resource.Error("Error al crear la cuenta")
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                _user.value = Resource.Error("Error de red: ${t.message}")
            }
        })
    }
}
