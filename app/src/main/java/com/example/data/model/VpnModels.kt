package com.example.data.model

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class VpnServer(
    val id: String,
    val country: String,
    val city: String,
    val loadPercentage: Int,
    val isRecommended: Boolean = false
)

data class VpnConnectionStatus(
    val state: VpnState,
    val currentServer: VpnServer?,
    val bytesReceived: Long = 0L,
    val bytesSent: Long = 0L,
    val connectionTimeMs: Long = 0L,
    val errorMessage: String? = null
)
