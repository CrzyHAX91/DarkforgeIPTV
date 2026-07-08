package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.cache.CacheManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class CacheManagerTest {

    private lateinit var context: Context
    private lateinit var cacheManager: CacheManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        cacheManager = CacheManager.getInstance(context)
        runBlocking {
            cacheManager.clearCache()
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            cacheManager.clearCache()
        }
    }

    @Test
    fun testMetadataCaching() = runBlocking {
        val key = "test_metadata_key"
        val value = "{\"title\":\"My Cached Video\",\"id\":\"123\"}"

        // Initially null
        assertNull(cacheManager.getMetadata(key))

        // Put metadata
        cacheManager.putMetadata(key, value)

        // Retrieve metadata
        val retrieved = cacheManager.getMetadata(key)
        assertEquals(value, retrieved)
    }

    @Test
    fun testThumbnailCaching() = runBlocking {
        val key = "test_thumb_key"
        val bytes = byteArrayOf(1, 2, 3, 4, 5)
        val fileName = "thumb_img.jpg"

        // Initially null
        assertNull(cacheManager.getThumbnailPath(key))

        // Put thumbnail
        val path = cacheManager.putThumbnail(key, bytes, fileName)
        assertNotNull(path)
        assertTrue(File(path!!).exists())

        // Retrieve path
        val retrievedPath = cacheManager.getThumbnailPath(key)
        assertEquals(path, retrievedPath)
    }

    @Test
    fun testMaxLimitEnforcement() = runBlocking {
        // Set a small max limit to trigger eviction easily (e.g., 20 bytes)
        cacheManager.setMaxCacheSize(20L)

        // Store some metadata items:
        // Item 1: 8 bytes
        cacheManager.putMetadata("item1", "12345678") // size 8
        // Item 2: 8 bytes
        cacheManager.putMetadata("item2", "abcdefgh") // size 8

        val metrics1 = cacheManager.getCacheMetricsFlow().first()
        assertEquals(16L, metrics1.currentSizeInBytes)
        assertEquals(2, metrics1.totalItems)

        // Item 3: 8 bytes (total size will exceed 20 limit -> 24. This triggers LRU eviction of item1)
        cacheManager.putMetadata("item3", "xyz12345") // size 8

        val metrics2 = cacheManager.getCacheMetricsFlow().first()
        // Item 1 should have been evicted, item 2 and item 3 remain
        assertNull(cacheManager.getMetadata("item1"))
        assertEquals("abcdefgh", cacheManager.getMetadata("item2"))
        assertEquals("xyz12345", cacheManager.getMetadata("item3"))
        assertEquals(16L, metrics2.currentSizeInBytes)
        assertEquals(2, metrics2.totalItems)
    }
}
