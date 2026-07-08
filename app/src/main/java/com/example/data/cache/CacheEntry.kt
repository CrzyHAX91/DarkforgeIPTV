package com.example.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_entries")
data class CacheEntry(
    @PrimaryKey val key: String,
    val type: String, // "THUMBNAIL" or "METADATA"
    val filePath: String?, // File path on disk for thumbnails
    val sizeInBytes: Long,
    val lastAccessedTime: Long = System.currentTimeMillis(),
    val metadataValue: String? = null // Inline JSON string for metadata
)
