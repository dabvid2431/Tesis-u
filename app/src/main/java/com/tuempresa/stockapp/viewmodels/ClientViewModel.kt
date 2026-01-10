package com.tuempresa.stockapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuempresa.stockapp.models.Client
import com.tuempresa.stockapp.repositories.ClientRepository
import com.tuempresa.stockapp.repositories.IClientRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientViewModel(private val repository: IClientRepository = ClientRepository()) : ViewModel() {
    private val _clients = MutableLiveData<List<Client>>()
    val clients: LiveData<List<Client>> get() = _clients

    fun fetchClients() {
        repository.getClients().enqueue(object : Callback<List<Client>> {
            override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                _clients.value = response.body()
            }
            override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                _clients.value = emptyList()
            }
        })
    }

        fun createClient(client: Client, onResult: (Client?) -> Unit, onError: (String) -> Unit) {
            repository.createClient(client).enqueue(object : Callback<Client> {
                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        // Extract server message if available
                        val err = try {
                            response.errorBody()?.string() ?: "Server error ${response.code()}"
                        } catch (e: Exception) {
                            "Server error ${response.code()}"
                        }
                        android.util.Log.e("ClientViewModel", "createClient failed: $err")
                        onError(err)
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<Client>, t: Throwable) {
                    val msg = t.message ?: "Network error"
                    android.util.Log.e("ClientViewModel", "createClient onFailure: $msg")
                    onError(msg)
                    onResult(null)
                }
            })
        }

    // Create client using a map payload (without id) to avoid sending a duplicate primary key
    fun createClientMap(clientMap: Map<String, Any>, onResult: (Client?) -> Unit, onError: (String) -> Unit) {
        repository.createClientMap(clientMap).enqueue(object : Callback<Client> {
            override fun onResponse(call: Call<Client>, response: Response<Client>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val err = try { response.errorBody()?.string() ?: "Server error ${response.code()}" } catch (e: Exception) { "Server error ${response.code()}" }
                    android.util.Log.e("ClientViewModel", "createClientMap failed: $err")
                    onError(err)
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<Client>, t: Throwable) {
                val msg = t.message ?: "Network error"
                android.util.Log.e("ClientViewModel", "createClientMap onFailure: $msg")
                onError(msg)
                onResult(null)
            }
        })
    }

        fun updateClient(id: Int, client: Client, onResult: (Client?) -> Unit) {
            repository.updateClient(id, client).enqueue(object : Callback<Client> {
                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    onResult(response.body())
                }
                override fun onFailure(call: Call<Client>, t: Throwable) {
                    onResult(null)
                }
            })
        }

        fun deleteClient(id: Int, onResult: (Boolean) -> Unit) {
            repository.deleteClient(id).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    onResult(response.isSuccessful)
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onResult(false)
                }
            })
        }
}
