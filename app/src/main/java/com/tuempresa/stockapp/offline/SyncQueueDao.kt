package com.tuempresa.stockapp.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: SyncQueueEntity): Long

    @Update
    fun update(entity: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE status IN (:statuses) ORDER BY createdAt ASC")
    fun getByStatuses(statuses: List<String>): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status IN ('pending', 'failed')")
    fun countPending(): Int

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'pending'")
    fun countPendingOnly(): Int

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'failed'")
    fun countFailedOnly(): Int

    @Query("SELECT * FROM sync_queue ORDER BY updatedAt DESC")
    fun getAllOrdered(): List<SyncQueueEntity>
}
