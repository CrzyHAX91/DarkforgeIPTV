package com.example.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class QosMetrics(
    val droppedFrames: Int,
    val bufferLatencyMs: Int,
    val recommendedCodec: String,
    val healthScore: Float
)

class QosMeshService {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    private val _metrics = MutableStateFlow(
        QosMetrics(0, 0, "H.264/AVC", 1.0f)
    )
    val metrics: StateFlow<QosMetrics> = _metrics.asStateFlow()

    fun startMonitoring() {
        scope.launch {
            while (isActive) {
                // Simulate Edge-Federated AI analysis of video latency & dropped frames
                delay(3000)
                
                val simulatedLatency = Random.nextInt(20, 1500)
                val simulatedDrops = if (simulatedLatency > 800) Random.nextInt(1, 15) else 0
                val health = 1.0f - (simulatedLatency / 2000f) - (simulatedDrops / 50f)
                
                // Determine codec via simulated edge AI model
                val codec = when {
                    health > 0.8f -> "AV1 (High Fidelity, Edge Mesh optimized)"
                    health > 0.5f -> "HEVC/H.265 (Balanced)"
                    else -> "H.264/AVC (Low Latency Fallback)"
                }
                
                _metrics.value = QosMetrics(
                    droppedFrames = simulatedDrops,
                    bufferLatencyMs = simulatedLatency,
                    recommendedCodec = codec,
                    healthScore = health.coerceIn(0f, 1f)
                )
            }
        }
    }
}
