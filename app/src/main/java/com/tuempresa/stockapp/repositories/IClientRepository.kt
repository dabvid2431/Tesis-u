package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.models.Client
import retrofit2.Call

interface IClientRepository {
    fun getClients(): Call<List<Client>>
    fun createClient(client: Client): Call<Client>
    fun createClientMap(clientMap: Map<String, Any>): Call<Client>
    fun updateClient(id: Int, client: Client): Call<Client>
    fun deleteClient(id: Int): Call<Unit>
}