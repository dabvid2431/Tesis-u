package com.tuempresa.stockapp.offline

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tuempresa.stockapp.api.RetrofitClient
import java.io.IOException

class OfflineSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val queueRepository = SyncQueueRepository(appContext)
    private val gson = Gson()

    override suspend fun doWork(): Result {
        val pendingItems = queueRepository.getProcessableItems()
        if (pendingItems.isEmpty()) return Result.success()

        var hasRetriableFailure = false

        for (item in pendingItems) {
            queueRepository.markInProgress(item)

            val success = when (item.operationType) {
                SyncOperationType.CREATE_SALE.name -> processCreateSale(item)
                SyncOperationType.CREATE_PURCHASE.name -> processCreatePurchase(item)
                SyncOperationType.CREATE_CLIENT.name -> processCreateClient(item)
                SyncOperationType.CREATE_PRODUCT.name -> processCreateProduct(item)
                else -> false
            }

            if (success) {
                queueRepository.remove(item.id)
            } else {
                hasRetriableFailure = true
            }
        }

        return if (hasRetriableFailure) Result.retry() else Result.success()
    }

    private fun processCreateSale(item: SyncQueueEntity): Boolean {
        return try {
            val payloadType = object : TypeToken<Map<String, Any>>() {}.type
            val payload: Map<String, Any> = gson.fromJson(item.payloadJson, payloadType)

            val response = RetrofitClient.instance.createSaleMap(payload).execute()
            if (response.isSuccessful) {
                true
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error de servidor"
                queueRepository.markFailed(item, errorMessage)
                false
            }
        } catch (ioe: IOException) {
            queueRepository.markFailed(item, ioe.message ?: "Error de red")
            false
        } catch (e: Exception) {
            queueRepository.markFailed(item, e.message ?: "Error inesperado")
            false
        }
    }

    private fun processCreatePurchase(item: SyncQueueEntity): Boolean {
        return try {
            val payloadType = object : TypeToken<Map<String, Any>>() {}.type
            val payload: Map<String, Any> = gson.fromJson(item.payloadJson, payloadType)

            val response = RetrofitClient.instance.createPurchaseMap(payload).execute()
            if (response.isSuccessful) {
                true
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error de servidor"
                queueRepository.markFailed(item, errorMessage)
                false
            }
        } catch (ioe: IOException) {
            queueRepository.markFailed(item, ioe.message ?: "Error de red")
            false
        } catch (e: Exception) {
            queueRepository.markFailed(item, e.message ?: "Error inesperado")
            false
        }
    }

    private fun processCreateClient(item: SyncQueueEntity): Boolean {
        return try {
            val payloadType = object : TypeToken<Map<String, Any>>() {}.type
            val payload: Map<String, Any> = gson.fromJson(item.payloadJson, payloadType)

            val response = RetrofitClient.instance.createClientMap(payload).execute()
            if (response.isSuccessful) {
                true
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error de servidor"
                queueRepository.markFailed(item, errorMessage)
                false
            }
        } catch (ioe: IOException) {
            queueRepository.markFailed(item, ioe.message ?: "Error de red")
            false
        } catch (e: Exception) {
            queueRepository.markFailed(item, e.message ?: "Error inesperado")
            false
        }
    }

    private fun processCreateProduct(item: SyncQueueEntity): Boolean {
        return try {
            val payloadType = object : TypeToken<Map<String, Any>>() {}.type
            val payload: Map<String, Any> = gson.fromJson(item.payloadJson, payloadType)

            val response = RetrofitClient.instance.createProductMap(payload).execute()
            if (response.isSuccessful) {
                true
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error de servidor"
                queueRepository.markFailed(item, errorMessage)
                false
            }
        } catch (ioe: IOException) {
            queueRepository.markFailed(item, ioe.message ?: "Error de red")
            false
        } catch (e: Exception) {
            queueRepository.markFailed(item, e.message ?: "Error inesperado")
            false
        }
    }
}
