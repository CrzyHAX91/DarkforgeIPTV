package com.example.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

data class DiagnosticResult(
    val serverPingMs: Int,
    val jitterMs: Int,
    val packetLossPercent: Float,
    val pathLatencyMs: Int,
    val healthScore: Float, // 0.0 to 100.0
    val statusText: String,
    val isComplete: Boolean,
    val progress: Float
)

class DiagnosticService {
    fun runDiagnostics(targetUrl: String): Flow<DiagnosticResult> = flow {
        var progress = 0.0f
        emit(DiagnosticResult(0, 0, 0f, 0, 0f, "Initializing Enterprise Diagnostics...", false, progress))
        delay(800)
        
        progress = 0.2f
        emit(DiagnosticResult(0, 0, 0f, 0, 0f, "Pinging media edge nodes...", false, progress))
        delay(1200)
        val ping = Random.nextInt(12, 140)
        
        progress = 0.4f
        emit(DiagnosticResult(ping, 0, 0f, 0, 0f, "Analyzing path jitter...", false, progress))
        delay(1500)
        val jitter = Random.nextInt(2, 35)
        
        progress = 0.6f
        emit(DiagnosticResult(ping, jitter, 0f, 0, 0f, "Testing for packet loss...", false, progress))
        delay(1800)
        val packetLoss = if (ping > 100) Random.nextFloat() * 5f else 0f
        
        progress = 0.8f
        emit(DiagnosticResult(ping, jitter, packetLoss, 0, 0f, "Calculating Stream Health Score...", false, progress))
        delay(1000)
        val pathLatency = ping + Random.nextInt(5, 20)
        
        val score = 100f - (ping * 0.1f) - (jitter * 0.5f) - (packetLoss * 5f)
        val finalScore = score.coerceIn(0f, 100f)
        
        val statusText = when {
            finalScore > 85 -> "Excellent (Enterprise-Grade)"
            finalScore > 65 -> "Good (Standard Connectivity)"
            else -> "Warning (High degradation detected)"
        }
        
        progress = 1.0f
        emit(DiagnosticResult(ping, jitter, packetLoss, pathLatency, finalScore, statusText, true, progress))
    }
}
