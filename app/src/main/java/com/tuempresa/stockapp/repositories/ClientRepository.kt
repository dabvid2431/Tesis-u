package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Client
import retrofit2.Call

class ClientRepository {
    private val api = RetrofitClient.instance

    fun getClients(): Call<List<Client>> = api.getClients()
    fun createClient(client: Client): Call<Client> = api.createClient(client)
    fun createClientMap(clientMap: Map<String, Any>): Call<Client> = api.createClientMap(clientMap)
    fun updateClient(id: Int, client: Client): Call<Client> = api.updateClient(id, client)
    fun deleteClient(id: Int): Call<Unit> = api.deleteClient(id)
}
