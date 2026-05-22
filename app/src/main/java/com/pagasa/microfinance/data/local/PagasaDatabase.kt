package com.pagasa.microfinance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedAccountEntity::class, CachedTransactionEntity::class, SyncQueueEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PagasaDatabase : RoomDatabase() {
    abstract fun dao(): PagasaDao

    companion object {
        @Volatile private var INSTANCE: PagasaDatabase? = null

        fun get(context: Context): PagasaDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context, PagasaDatabase::class.java, "pagasa_offline.db")
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}
