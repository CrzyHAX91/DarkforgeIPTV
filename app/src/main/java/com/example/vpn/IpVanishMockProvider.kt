package com.example.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class VpnState { UNPROTECTED, CONNECTING, SECURE }

class IpVanishMockProvider {
    val connectionState: StateFlow<VpnState> = MutableStateFlow(VpnState.UNPROTECTED)
    fun generateQrAuthPayload() = ""
    suspend fun disconnect() {}
    suspend fun simulateQrScanAuthorization() {}
    suspend fun connectSecureTunnel() {}
}
