package com.example.data.cache

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File

class CacheCleanupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("CacheCleanupWorker", "Starting scheduled cache cleanup task...")
        return try {
            val cacheManager = CacheManager.getInstance(applicationContext)
            
            // Define expiration period: e.g. 24 hours. Under extreme Fire OS limits, keeping it fresh is key.
            val expiryDurationMs = 24 * 60 * 60 * 1000L // 24 hours
            val cutoffTime = System.currentTimeMillis() - expiryDurationMs
            
            val db = CacheDatabase.getDatabase(applicationContext)
            val dao = db.cacheDao()
            
            val expiredEntries = dao.getEntriesOlderThan(cutoffTime)
            Log.d("CacheCleanupWorker", "Found ${expiredEntries.size} expired cache entries (older than 24 hours).")
            
            var filesDeleted = 0
            var bytesFreed = 0L
            
            for (entry in expiredEntries) {
                if (entry.type == "THUMBNAIL" && entry.filePath != null) {
                    val file = File(entry.filePath)
                    if (file.exists()) {
                        val size = file.length()
                        if (file.delete()) {
                            filesDeleted++
                            bytesFreed += size
                        }
                    }
                }
                dao.deleteEntry(entry.key)
            }
            
            // Re-enforce general LRU size boundaries to prevent overruns
            cacheManager.evictIfNecessary()
            
            Log.d("CacheCleanupWorker", "Cache cleanup task completed. Deleted $filesDeleted expired thumbnail files, freed $bytesFreed bytes.")
            Result.success()
        } catch (e: Exception) {
            Log.e("CacheCleanupWorker", "Exception during cache cleanup execution", e)
            Result.retry()
        }
    }
}
