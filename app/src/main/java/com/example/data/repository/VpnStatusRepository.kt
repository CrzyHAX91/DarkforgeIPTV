package com.example.data.repository

import com.example.data.model.VpnConnectionStatus
import com.example.data.model.VpnServer
import com.example.data.model.VpnState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

interface VpnStatusRepository {
    val connectionStatus: StateFlow<VpnConnectionStatus>
    fun getAvailableServers(): Flow<List<VpnServer>>
    suspend fun connect(server: VpnServer)
    suspend fun disconnect()
}

class MockVpnStatusRepository : VpnStatusRepository {

    private val _connectionStatus = MutableStateFlow(
        VpnConnectionStatus(
            state = VpnState.DISCONNECTED,
            currentServer = null
        )
    )
    override val connectionStatus: StateFlow<VpnConnectionStatus> = _connectionStatus.asStateFlow()

    private val mockServers = listOf(
        VpnServer("eu1", "Netherlands", "Amsterdam", 32, true),
        VpnServer("eu2", "Germany", "Frankfurt", 68, false),
        VpnServer("us1", "United States", "New York", 45, false),
        VpnServer("us2", "United States", "Los Angeles", 75, false),
        VpnServer("uk1", "United Kingdom", "London", 82, false)
    )

    override fun getAvailableServers(): Flow<List<VpnServer>> = flow {
        delay(300)
        emit(mockServers)
    }

    override suspend fun connect(server: VpnServer) {
        if (_connectionStatus.value.state == VpnState.CONNECTED) return
        
        _connectionStatus.value = VpnConnectionStatus(
            state = VpnState.CONNECTING,
            currentServer = server
        )
        
        delay(1500) // Simulate connection delay
        
        _connectionStatus.value = VpnConnectionStatus(
            state = VpnState.CONNECTED,
            currentServer = server,
            connectionTimeMs = System.currentTimeMillis()
        )
    }

    override suspend fun disconnect() {
        if (_connectionStatus.value.state == VpnState.DISCONNECTED) return
        
        _connectionStatus.value = _connectionStatus.value.copy(
            state = VpnState.DISCONNECTED,
            currentServer = null,
            bytesReceived = 0,
            bytesSent = 0,
            connectionTimeMs = 0
        )
    }
}
