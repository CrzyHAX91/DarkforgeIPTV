package com.example.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Base64
import java.util.UUID
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * VPN connection states for state management.
 */
enum class VpnState {
    UNPROTECTED,
    CONNECTING,
    SECURE,
    DISCONNECTING,
    ERROR
}

/**
 * Data class representing VPN connection result.
 */
data class VpnConnectionResult(
    val isSuccess: Boolean,
    val state: VpnState,
    val message: String = "",
    val errorCode: Int? = null,
    val tunnelId: String? = null,
    val encryptionLevel: String = "AES-256"
)

/**
 * Data class representing QR authentication payload.
 */
data class QrAuthPayload(
    val deviceId: String,
    val timestamp: Long,
    val token: String,
    val signature: String,
    val encryptedData: String
)

/**
 * Sealed class for VPN operations results.
 */
sealed class VpnOperationResult {
    data class Success(val data: String, val state: VpnState) : VpnOperationResult()
    data class Failure(val exception: Exception, val errorMessage: String) : VpnOperationResult()
    object Pending : VpnOperationResult()
}

/**
 * Mock provider for IpVanish VPN operations with enhanced security and error handling.
 * This is a mock implementation for development and testing purposes.
 * 
 * Key features:
 * - State flow management with proper encapsulation
 * - QR code authentication payload generation with encryption
 * - Simulated secure tunnel connection
 * - Graceful disconnection handling
 * - Comprehensive error handling
 */
class IpVanishMockProvider {
    
    private val TAG = "IpVanishMockProvider"
    
    // Private mutable state flow
    private val _connectionState = MutableStateFlow<VpnState>(VpnState.UNPROTECTED)
    
    // Public immutable state flow
    val connectionState: StateFlow<VpnState> = _connectionState.asStateFlow()
    
    // Operation result flow
    private val _operationResult = MutableStateFlow<VpnOperationResult>(VpnOperationResult.Pending)
    val operationResult: StateFlow<VpnOperationResult> = _operationResult.asStateFlow()
    
    // Connection metadata
    private var currentTunnelId: String? = null
    private var currentDeviceId: String = UUID.randomUUID().toString()
    private var connectionTimestamp: Long = 0L
    private var lastAuthToken: String? = null
    
    // Configuration constants
    companion object {
        private const val QR_PAYLOAD_EXPIRATION_MS = 300_000L // 5 minutes
        private const val CONNECTION_TIMEOUT_MS = 15_000L      // 15 seconds
        private const val ENCRYPTION_ALGORITHM = "AES"
        private const val ENCRYPTION_KEY_SIZE = 256
        private const val MOCK_TUNNEL_LATENCY_MS = 2000L       // Realistic tunnel establishment time
    }
    
    /**
     * Generates a QR code authentication payload with encryption and signature.
     * 
     * @return QrAuthPayload containing device ID, token, and encrypted data
     */
    fun generateQrAuthPayload(): QrAuthPayload {
        return try {
            val timestamp = System.currentTimeMillis()
            val token = generateSecureToken()
            val deviceIdWithTimestamp = "$currentDeviceId:$timestamp"
            val encryptedData = encryptData(deviceIdWithTimestamp)
            val signature = generateSignature(token, encryptedData)
            
            // Cache the token for later verification
            lastAuthToken = token
            
            QrAuthPayload(
                deviceId = currentDeviceId,
                timestamp = timestamp,
                token = token,
                signature = signature,
                encryptedData = encryptedData
            )
        } catch (e: Exception) {
            logError("Failed to generate QR auth payload", e)
            // Return a safe default payload
            QrAuthPayload(
                deviceId = currentDeviceId,
                timestamp = System.currentTimeMillis(),
                token = "",
                signature = "",
                encryptedData = ""
            )
        }
    }
    
    /**
     * Simulates QR scan authorization by verifying the payload and updating state.
     * 
     * @return VpnOperationResult indicating success or failure
     */
    suspend fun simulateQrScanAuthorization(): VpnOperationResult {
        return withContext(Dispatchers.Default) {
            try {
                _operationResult.emit(VpnOperationResult.Pending)
                _connectionState.emit(VpnState.CONNECTING)
                
                // Simulate QR scan processing delay
                delay(1500)
                
                // Verify the token
                if (lastAuthToken.isNullOrEmpty()) {
                    throw IllegalStateException("No valid authentication token available")
                }
                
                // Generate tunnel ID after successful verification
                currentTunnelId = "tunnel_${UUID.randomUUID()}".take(32)
                connectionTimestamp = System.currentTimeMillis()
                
                val successMessage = "QR Authorization successful. Tunnel ID: $currentTunnelId"
                _operationResult.emit(VpnOperationResult.Success(successMessage, VpnState.SECURE))
                
                successMessage
            } catch (e: Exception) {
                logError("QR scan authorization failed", e)
                _operationResult.emit(
                    VpnOperationResult.Failure(e, "Authorization failed: ${e.message}")
                )
                ""
            } finally {
                // Clear sensitive data
                lastAuthToken = null
            }
        }
    }
    
    /**
     * Establishes a secure VPN tunnel with realistic connection simulation.
     * 
     * @return VpnConnectionResult containing connection status and details
     */
    suspend fun connectSecureTunnel(): VpnConnectionResult {
        return withContext(Dispatchers.Default) {
            try {
                _connectionState.emit(VpnState.CONNECTING)
                _operationResult.emit(VpnOperationResult.Pending)
                
                // Validate prerequisite: QR authorization must have occurred
                if (currentTunnelId.isNullOrEmpty()) {
                    throw IllegalStateException("QR authorization required before tunnel connection")
                }
                
                // Simulate connection establishment with realistic delay
                delay(MOCK_TUNNEL_LATENCY_MS)
                
                // Simulate occasional connection failures (10% failure rate for testing)
                if (isRandomFailure(failureRate = 0.1)) {
                    throw Exception("Simulated tunnel connection failure")
                }
                
                _connectionState.emit(VpnState.SECURE)
                
                val result = VpnConnectionResult(
                    isSuccess = true,
                    state = VpnState.SECURE,
                    message = "Secure tunnel established successfully",
                    tunnelId = currentTunnelId,
                    encryptionLevel = "AES-256-GCM"
                )
                
                _operationResult.emit(VpnOperationResult.Success(
                    "Connected: $currentTunnelId",
                    VpnState.SECURE
                ))
                
                result
            } catch (e: Exception) {
                logError("Secure tunnel connection failed", e)
                _connectionState.emit(VpnState.ERROR)
                
                val errorResult = VpnConnectionResult(
                    isSuccess = false,
                    state = VpnState.ERROR,
                    message = "Connection failed: ${e.message}",
                    errorCode = 1001
                )
                
                _operationResult.emit(VpnOperationResult.Failure(e, e.message ?: "Unknown error"))
                errorResult
            }
        }
    }
    
    /**
     * Gracefully disconnects the VPN tunnel and cleanup resources.
     * 
     * @return VpnOperationResult indicating disconnection status
     */
    suspend fun disconnect(): VpnOperationResult {
        return withContext(Dispatchers.Default) {
            try {
                // Only disconnect if connected
                if (_connectionState.value != VpnState.SECURE) {
                    return@withContext VpnOperationResult.Failure(
                        IllegalStateException("Not connected"),
                        "Cannot disconnect: VPN is not active"
                    )
                }
                
                _connectionState.emit(VpnState.DISCONNECTING)
                _operationResult.emit(VpnOperationResult.Pending)
                
                // Simulate graceful shutdown delay
                delay(1000)
                
                // Cleanup resources
                clearConnectionData()
                
                _connectionState.emit(VpnState.UNPROTECTED)
                
                val successMessage = "VPN disconnected successfully"
                _operationResult.emit(VpnOperationResult.Success(successMessage, VpnState.UNPROTECTED))
                
                VpnOperationResult.Success(successMessage, VpnState.UNPROTECTED)
            } catch (e: Exception) {
                logError("Disconnection failed", e)
                _connectionState.emit(VpnState.ERROR)
                
                VpnOperationResult.Failure(e, "Disconnection error: ${e.message}")
            }
        }
    }
    
    /**
     * Force reset the VPN connection state. Use with caution.
     */
    suspend fun resetConnection() {
        clearConnectionData()
        _connectionState.emit(VpnState.UNPROTECTED)
        _operationResult.emit(VpnOperationResult.Pending)
    }
    
    /**
     * Get current connection status with metadata.
     */
    fun getConnectionStatus(): Map<String, Any> {
        return mapOf(
            "state" to _connectionState.value,
            "tunnelId" to (currentTunnelId ?: "N/A"),
            "deviceId" to currentDeviceId,
            "connectionTime" to (if (connectionTimestamp > 0) System.currentTimeMillis() - connectionTimestamp else 0),
            "isConnected" to (_connectionState.value == VpnState.SECURE)
        )
    }
    
    // ============ Private Helper Methods ============
    
    /**
     * Generates a cryptographically secure token.
     */
    private fun generateSecureToken(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    /**
     * Encrypts data using AES encryption.
     */
    private fun encryptData(data: String): String {
        return try {
            val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
            keyGenerator.init(ENCRYPTION_KEY_SIZE)
            val secretKey: SecretKey = keyGenerator.generateKey()
            
            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val encryptedBytes = cipher.doFinal(data.toByteArray())
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            logError("Encryption failed", e)
            Base64.encodeToString(data.toByteArray(), Base64.NO_WRAP)
        }
    }
    
    /**
     * Generates a signature for data integrity verification.
     */
    private fun generateSignature(token: String, encryptedData: String): String {
        return try {
            val data = "$token:$encryptedData"
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(data.toByteArray())
            Base64.encodeToString(hash, Base64.NO_WRAP)
        } catch (e: Exception) {
            logError("Signature generation failed", e)
            ""
        }
    }
    
    /**
     * Simulates random connection failure for testing resilience.
     */
    private fun isRandomFailure(failureRate: Double): Boolean {
        return Math.random() < failureRate
    }
    
    /**
     * Clears sensitive connection data.
     */
    private fun clearConnectionData() {
        currentTunnelId = null
        connectionTimestamp = 0L
        lastAuthToken = null
    }
    
    /**
     * Logs errors for debugging purposes.
     */
    private fun logError(message: String, exception: Exception) {
        android.util.Log.e(TAG, message, exception)
    }
}
