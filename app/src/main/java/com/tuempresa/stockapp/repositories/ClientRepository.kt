package com.tuempresa.stockapp.repositories

import com.tuempresa.stockapp.api.RetrofitClient
import com.tuempresa.stockapp.models.Client
import retrofit2.Call

class ClientRepository : IClientRepository {
    private val api get() = RetrofitClient.instance

    override fun getClients(): Call<List<Client>> = api.getClients()
    override fun createClient(client: Client): Call<Client> = api.createClient(client)
    override fun createClientMap(clientMap: Map<String, Any>): Call<Client> = api.createClientMap(clientMap)
    override fun updateClient(id: Int, client: Client): Call<Client> = api.updateClient(id, client)
    override fun deleteClient(id: Int): Call<Unit> = api.deleteClient(id)
}
