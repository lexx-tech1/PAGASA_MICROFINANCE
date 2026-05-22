package com.pagasa.microfinance.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_accounts")
data class CachedAccountEntity(
    @PrimaryKey val uid: String,
    val branchId: String,
    val payloadJson: String,
    val updatedAtMillis: Long
)

@Entity(tableName = "cached_transactions")
data class CachedTransactionEntity(
    @PrimaryKey val id: String,
    val uid: String,
    val branchId: String,
    val payloadJson: String,
    val timestampMillis: Long
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uid: String,
    val branchId: String,
    val action: String,
    val payloadJson: String,
    val createdAtMillis: Long
)
