package com.pagasa.microfinance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PagasaDao {
    @Query("SELECT * FROM cached_accounts WHERE uid = :uid LIMIT 1")
    fun observeCachedAccount(uid: String): Flow<CachedAccountEntity?>

    @Query("SELECT * FROM cached_transactions WHERE uid = :uid AND branchId = :branchId ORDER BY timestampMillis DESC")
    fun observeTransactions(uid: String, branchId: String): Flow<List<CachedTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccount(entity: CachedAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransactions(entities: List<CachedTransactionEntity>)

    @Insert
    suspend fun enqueueSync(entity: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY createdAtMillis ASC")
    suspend fun pendingSync(): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteSyncItem(id: Long)
}
