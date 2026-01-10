package com.tuempresa.stockapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuempresa.stockapp.models.Client
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class ClientViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // retrofit-mock helpers
    private fun <T> fakeCallSuccess(response: Response<T>): Call<T> = retrofit2.mock.Calls.response(response.body())
    private fun <T> fakeCallFailure(throwable: Throwable): Call<T> = retrofit2.mock.Calls.failure(throwable)

    @Test
    fun fetchClients_setsLiveDataOnSuccess() {
        val expected = listOf(
            Client(id = 1, name = "John Doe", phone = "123456789", email = "john@example.com", address = "123 Main St")
        )
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = fakeCallSuccess<List<Client>>(Response.success(expected))
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        vm.fetchClients()
        val value = vm.clients.value
        assertEquals(expected, value)
    }

    @Test
    fun fetchClients_onFailure_setsEmptyList() {
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = fakeCallFailure<List<Client>>(RuntimeException("network"))
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        vm.fetchClients()
        val value = vm.clients.value
        assertTrue(value!!.isEmpty())
    }

    @Test
    fun createClient_onSuccess_callsOnResult() {
        val newClient = Client(id = 2, name = "Jane Doe", phone = "987654321", email = "jane@example.com", address = "456 Oak Ave")
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = fakeCallSuccess<Client>(Response.success(newClient))
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        var result: Client? = null
        var errorCalled = false
        vm.createClient(newClient, { res -> result = res }, { errorCalled = true })
        assertEquals(newClient, result)
        assertFalse(errorCalled)
    }

    @Test
    fun createClient_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = fakeCallFailure<Client>(RuntimeException("network"))
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        var result: Client? = Client(id = -1, name = "", phone = null, email = null, address = null)
        var errorMessage: String? = null
        var onErrorCalled = false
        var onResultCalled = false
        
        try {
            vm.createClient(
                Client(id = 0, name = "Test", phone = null, email = null, address = null),
                { res -> 
                    result = res
                    onResultCalled = true
                },
                { err -> 
                    errorMessage = err
                    onErrorCalled = true
                }
            )
            // Give time for async callbacks to execute (retrofit-mock executes synchronously but just in case)
            Thread.sleep(200)
        } catch (e: Exception) {
            // Ignore exceptions from Log.e (requires Android context)
            // The exception might be thrown but callbacks should still execute
        }
        
        // Verify callbacks were called
        assertTrue("onError callback should be called", onErrorCalled)
        assertTrue("onResult callback should be called", onResultCalled)
        assertNull("Result should be null on failure", result)
        assertNotNull("Error message should not be null", errorMessage)
        assertEquals("Error message should be 'network'", "network", errorMessage)
    }

    @Test
    fun createClientMap_onSuccess_callsOnResult() {
        val newClient = Client(id = 3, name = "Bob Smith", phone = "5551234", email = "bob@example.com", address = "789 Pine Rd")
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = fakeCallSuccess<Client>(Response.success(newClient))
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        var result: Client? = null
        var errorCalled = false
        vm.createClientMap(mapOf("name" to "Bob Smith"), { res -> result = res }, { errorCalled = true })
        assertEquals(newClient, result)
        assertFalse(errorCalled)
    }

    @Test
    fun createClientMap_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = fakeCallFailure<Client>(RuntimeException("network"))
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        var result: Client? = Client(id = -1, name = "", phone = null, email = null, address = null)
        var errorMessage: String? = null
        var onErrorCalled = false
        var onResultCalled = false
        
        try {
            vm.createClientMap(
                mapOf("name" to "Test"),
                { res -> 
                    result = res
                    onResultCalled = true
                },
                { err -> 
                    errorMessage = err
                    onErrorCalled = true
                }
            )
            // Give time for async callbacks to execute (retrofit-mock executes synchronously but just in case)
            Thread.sleep(200)
        } catch (e: Exception) {
            // Ignore exceptions from Log.e (requires Android context)
            // The exception might be thrown but callbacks should still execute
        }
        
        // Verify callbacks were called
        assertTrue("onError callback should be called", onErrorCalled)
        assertTrue("onResult callback should be called", onResultCalled)
        assertNull("Result should be null on failure", result)
        assertNotNull("Error message should not be null", errorMessage)
        assertEquals("Error message should be 'network'", "network", errorMessage)
    }

    @Test
    fun updateClient_onSuccess_callsOnResult() {
        val updatedClient = Client(id = 4, name = "Updated Name", phone = "111222333", email = "updated@example.com", address = "999 New St")
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = fakeCallSuccess<Client>(Response.success(updatedClient))
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        var result: Client? = null
        vm.updateClient(4, updatedClient) { res -> result = res }
        assertEquals(updatedClient, result)
    }

    @Test
    fun updateClient_onFailure_callsOnResultNull() {
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = fakeCallFailure<Client>(RuntimeException("network"))
            override fun deleteClient(id: Int) = throw UnsupportedOperationException()
        }

        val vm = ClientViewModel(repo)
        var result: Client? = Client(id = -1, name = "", phone = null, email = null, address = null)
        vm.updateClient(0, Client(id = 0, name = "Test", phone = null, email = null, address = null)) { res -> result = res }
        assertNull(result)
    }

    @Test
    fun deleteClient_onSuccess_callsOnResultTrue() {
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = fakeCallSuccess<Unit>(Response.success(Unit))
        }

        val vm = ClientViewModel(repo)
        var result = false
        vm.deleteClient(1) { res -> result = res }
        assertTrue(result)
    }

    @Test
    fun deleteClient_onFailure_callsOnResultFalse() {
        val repo = object : com.tuempresa.stockapp.repositories.IClientRepository {
            override fun getClients() = throw UnsupportedOperationException()
            override fun createClient(client: Client) = throw UnsupportedOperationException()
            override fun createClientMap(clientMap: Map<String, Any>) = throw UnsupportedOperationException()
            override fun updateClient(id: Int, client: Client) = throw UnsupportedOperationException()
            override fun deleteClient(id: Int) = fakeCallFailure<Unit>(RuntimeException("network"))
        }

        val vm = ClientViewModel(repo)
        var result = true
        vm.deleteClient(1) { res -> result = res }
        assertFalse(result)
    }
}
