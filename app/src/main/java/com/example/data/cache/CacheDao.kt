package com.example.data.cache

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache_entries WHERE `key` = :key")
    suspend fun getEntry(key: String): CacheEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CacheEntry)

    @Query("UPDATE cache_entries SET lastAccessedTime = :time WHERE `key` = :key")
    suspend fun updateLastAccessed(key: String, time: Long)

    @Query("SELECT * FROM cache_entries ORDER BY lastAccessedTime ASC")
    suspend fun getAllEntriesLru(): List<CacheEntry>

    @Query("SELECT SUM(sizeInBytes) FROM cache_entries")
    fun getTotalSizeFlow(): Flow<Long?>

    @Query("SELECT SUM(sizeInBytes) FROM cache_entries")
    suspend fun getTotalSize(): Long?

    @Query("SELECT COUNT(*) FROM cache_entries")
    fun getItemsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cache_entries")
    suspend fun getItemsCount(): Int

    @Query("SELECT * FROM cache_entries WHERE lastAccessedTime < :cutoffTime")
    suspend fun getEntriesOlderThan(cutoffTime: Long): List<CacheEntry>

    @Query("DELETE FROM cache_entries WHERE `key` = :key")
    suspend fun deleteEntry(key: String)

    @Query("DELETE FROM cache_entries")
    suspend fun clearAll()
}
