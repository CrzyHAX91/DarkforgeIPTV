package com.example.data.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore by preferencesDataStore(name = "cache_settings")

data class CacheMetrics(
    val currentSizeInBytes: Long,
    val maxSizeInBytes: Long,
    val percentageUsed: Float,
    val totalItems: Int
)

class CacheManager private constructor(private val context: Context) {

    private val db = CacheDatabase.getDatabase(context)
    private val dao = db.cacheDao()

    companion object {
        val KEY_MAX_CACHE_SIZE = longPreferencesKey("max_cache_size")
        const val DEFAULT_MAX_CACHE_SIZE = 16L * 1024L * 1024L * 1024L // 16GB in bytes

        @Volatile
        private var INSTANCE: CacheManager? = null

        fun getInstance(context: Context): CacheManager {
            return INSTANCE ?: synchronized(this) {
                val instance = CacheManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    val maxCacheSizeFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_MAX_CACHE_SIZE] ?: DEFAULT_MAX_CACHE_SIZE
    }

    suspend fun setMaxCacheSize(sizeInBytes: Long) = withContext(Dispatchers.IO) {
        context.dataStore.edit { preferences ->
            preferences[KEY_MAX_CACHE_SIZE] = sizeInBytes
        }
        evictIfNecessary()
    }

    suspend fun getMaxCacheSize(): Long = withContext(Dispatchers.IO) {
        maxCacheSizeFlow.first()
    }

    suspend fun getMetadata(key: String): String? = withContext(Dispatchers.IO) {
        val entry = dao.getEntry(key)
        if (entry != null && entry.type == "METADATA") {
            dao.updateLastAccessed(key, System.currentTimeMillis())
            entry.metadataValue
        } else {
            null
        }
    }

    suspend fun putMetadata(key: String, value: String) = withContext(Dispatchers.IO) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        val size = bytes.size.toLong()
        val entry = CacheEntry(
            key = key,
            type = "METADATA",
            filePath = null,
            sizeInBytes = size,
            lastAccessedTime = System.currentTimeMillis(),
            metadataValue = value
        )
        dao.insertEntry(entry)
        evictIfNecessary()
    }

    suspend fun getThumbnailPath(key: String): String? = withContext(Dispatchers.IO) {
        val entry = dao.getEntry(key)
        if (entry != null && entry.type == "THUMBNAIL") {
            val filePath = entry.filePath
            if (filePath != null && File(filePath).exists()) {
                dao.updateLastAccessed(key, System.currentTimeMillis())
                filePath
            } else {
                dao.deleteEntry(key)
                null
            }
        } else {
            null
        }
    }

    suspend fun putThumbnail(key: String, fileBytes: ByteArray, fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            val thumbnailsDir = File(context.cacheDir, "thumbnails").apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9.\\-_]"), "_")
            val destFile = File(thumbnailsDir, sanitizedFileName)
            
            FileOutputStream(destFile).use { fos ->
                fos.write(fileBytes)
            }

            val size = destFile.length()
            val entry = CacheEntry(
                key = key,
                type = "THUMBNAIL",
                filePath = destFile.absolutePath,
                sizeInBytes = size,
                lastAccessedTime = System.currentTimeMillis(),
                metadataValue = null
            )
            dao.insertEntry(entry)
            
            evictIfNecessary()
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun evictIfNecessary() = withContext(Dispatchers.IO) {
        val maxLimit = getMaxCacheSize()
        var currentTotalSize = dao.getTotalSize() ?: 0L
        
        if (currentTotalSize <= maxLimit) {
            return@withContext
        }

        val lruEntries = dao.getAllEntriesLru()
        for (entry in lruEntries) {
            if (currentTotalSize <= maxLimit) {
                break
            }
            if (entry.type == "THUMBNAIL" && entry.filePath != null) {
                val file = File(entry.filePath)
                if (file.exists()) {
                    file.delete()
                }
            }
            dao.deleteEntry(entry.key)
            currentTotalSize -= entry.sizeInBytes
        }
    }

    fun getCacheMetricsFlow(): Flow<CacheMetrics> {
        return combine(
            dao.getTotalSizeFlow(),
            dao.getItemsCountFlow(),
            maxCacheSizeFlow
        ) { totalSize, count, maxSize ->
            val size = totalSize ?: 0L
            val percentage = if (maxSize > 0) (size.toFloat() / maxSize.toFloat()) * 100f else 0f
            CacheMetrics(
                currentSizeInBytes = size,
                maxSizeInBytes = maxSize,
                percentageUsed = percentage.coerceIn(0f, 100f),
                totalItems = count
            )
        }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        val thumbnailsDir = File(context.cacheDir, "thumbnails")
        if (thumbnailsDir.exists()) {
            thumbnailsDir.listFiles()?.forEach { file ->
                file.delete()
            }
        }
        dao.clearAll()
    }
}
