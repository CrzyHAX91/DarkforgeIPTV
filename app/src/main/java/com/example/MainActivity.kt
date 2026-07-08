package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.cache.CacheCleanupWorker
import com.example.ui.PremiumTvApp
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule periodic cache cleanup task for Fire OS performance optimization
        scheduleCacheCleanup()
        
        // Enable edge-to-edge layout execution
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set up WindowInsets handling to protect TV layouts from physical screen clipping (overscan)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            
            // Allow the system to propagate these insets down to the Jetpack Compose hierarchy
            ViewCompat.onApplyWindowInsets(view, insets)
        }

        setContent {
            PremiumTvApp()
        }
    }

    override fun dispatchKeyEvent(event: android.view.KeyEvent): Boolean {
        if (event.action == android.view.KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER || 
                keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                
                val rootInsets = ViewCompat.getRootWindowInsets(window.decorView)
                val isImeVisible = rootInsets?.isVisible(WindowInsetsCompat.Type.ime()) == true
                if (isImeVisible) {
                    // Explicitly block 'Enter' key-down events when the virtual keyboard is active,
                    // ensuring input forms like Xtream Codes login capture characters correctly
                    // instead of triggering premature form submissions.
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun scheduleCacheCleanup() {
        val cleanupRequest = PeriodicWorkRequestBuilder<CacheCleanupWorker>(
            12, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "CacheCleanupTask",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}

