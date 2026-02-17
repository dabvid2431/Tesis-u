package com.tuempresa.stockapp.offline

import android.content.Context
import com.google.gson.Gson

class SyncQueueRepository(private val appContext: Context) {
    private val dao = SyncDatabase.getInstance(appContext).syncQueueDao()
    private val gson = Gson()

    fun enqueueCreateSale(payload: Map<String, Any>): Long {
        val entity = SyncQueueEntity(
            operationType = SyncOperationType.CREATE_SALE.name,
            payloadJson = gson.toJson(payload),
            status = SyncQueueEntity.STATUS_PENDING
        )
        val id = dao.insert(entity)
        OfflineSyncScheduler.enqueueImmediateSync(appContext)
        return id
    }

    fun enqueueCreatePurchase(payload: Map<String, Any>): Long {
        val entity = SyncQueueEntity(
            operationType = SyncOperationType.CREATE_PURCHASE.name,
            payloadJson = gson.toJson(payload),
            status = SyncQueueEntity.STATUS_PENDING
        )
        val id = dao.insert(entity)
        OfflineSyncScheduler.enqueueImmediateSync(appContext)
        return id
    }

    fun enqueueCreateClient(payload: Map<String, Any>): Long {
        val entity = SyncQueueEntity(
            operationType = SyncOperationType.CREATE_CLIENT.name,
            payloadJson = gson.toJson(payload),
            status = SyncQueueEntity.STATUS_PENDING
        )
        val id = dao.insert(entity)
        OfflineSyncScheduler.enqueueImmediateSync(appContext)
        return id
    }

    fun enqueueCreateProduct(payload: Map<String, Any>): Long {
        val entity = SyncQueueEntity(
            operationType = SyncOperationType.CREATE_PRODUCT.name,
            payloadJson = gson.toJson(payload),
            status = SyncQueueEntity.STATUS_PENDING
        )
        val id = dao.insert(entity)
        OfflineSyncScheduler.enqueueImmediateSync(appContext)
        return id
    }

    fun getPendingCount(): Int {
        return dao.countPending()
    }

    fun getPendingOnlyCount(): Int {
        return dao.countPendingOnly()
    }

    fun getFailedOnlyCount(): Int {
        return dao.countFailedOnly()
    }

    fun getAllItems(): List<SyncQueueEntity> {
        return dao.getAllOrdered()
    }

    fun getProcessableItems(): List<SyncQueueEntity> {
        return dao.getByStatuses(listOf(SyncQueueEntity.STATUS_PENDING, SyncQueueEntity.STATUS_FAILED))
    }

    fun markInProgress(item: SyncQueueEntity) {
        dao.update(
            item.copy(
                status = SyncQueueEntity.STATUS_IN_PROGRESS,
                updatedAt = System.currentTimeMillis(),
                lastError = null
            )
        )
    }

    fun markFailed(item: SyncQueueEntity, error: String) {
        dao.update(
            item.copy(
                status = SyncQueueEntity.STATUS_FAILED,
                attemptCount = item.attemptCount + 1,
                lastError = error,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun remove(itemId: Long) {
        dao.deleteById(itemId)
    }
}
