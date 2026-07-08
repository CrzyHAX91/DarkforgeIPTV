package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdvancedSettingsManager {
    private val _ztmeEnabled = MutableStateFlow(true)
    val ztmeEnabled: StateFlow<Boolean> = _ztmeEnabled.asStateFlow()

    private val _qoSmeshEnabled = MutableStateFlow(false)
    val qoSmeshEnabled: StateFlow<Boolean> = _qoSmeshEnabled.asStateFlow()

    private val _ambientHubEnabled = MutableStateFlow(true)
    val ambientHubEnabled: StateFlow<Boolean> = _ambientHubEnabled.asStateFlow()
    
    private val _whisperSyncEnabled = MutableStateFlow(false)
    val whisperSyncEnabled: StateFlow<Boolean> = _whisperSyncEnabled.asStateFlow()

    // 1. Hardware Decoding Options
    private val _forceHardwareDecoding = MutableStateFlow(true)
    val forceHardwareDecoding: StateFlow<Boolean> = _forceHardwareDecoding.asStateFlow()

    private val _mediaCodecTunneling = MutableStateFlow(false)
    val mediaCodecTunneling: StateFlow<Boolean> = _mediaCodecTunneling.asStateFlow()

    // 2. Audio Pass-Through
    private val _audioPassthroughEnabled = MutableStateFlow(true)
    val audioPassthroughEnabled: StateFlow<Boolean> = _audioPassthroughEnabled.asStateFlow()

    // 3. HDR & Dolby Vision
    private val _hdrConversionMode = MutableStateFlow("AUTO") // "AUTO", "FORCE_HDR10", "SDR_FALLBACK", "DOLBY_VISION_PASS"
    val hdrConversionMode: StateFlow<String> = _hdrConversionMode.asStateFlow()

    // 4. USB / SSD High Performance Local Storage Buffering
    private val _usbPerformanceBuffering = MutableStateFlow(true)
    val usbPerformanceBuffering: StateFlow<Boolean> = _usbPerformanceBuffering.asStateFlow()

    // 5. Provider-Specific Connection fixes (Self-Signed Certificates & TS chunk recovery)
    private val _bypassSslVerification = MutableStateFlow(false)
    val bypassSslVerification: StateFlow<Boolean> = _bypassSslVerification.asStateFlow()

    // 6. DRM Override Mode
    private val _widevineDrmLevel = MutableStateFlow("L1_PREFERRED") // "L1_PREFERRED", "L3_FALLBACK"
    val widevineDrmLevel: StateFlow<String> = _widevineDrmLevel.asStateFlow()

    // 7. HDMI CEC Simulation Logs
    private val _cecPowerSyncEnabled = MutableStateFlow(true)
    val cecPowerSyncEnabled: StateFlow<Boolean> = _cecPowerSyncEnabled.asStateFlow()

    fun setZtmeEnabled(enabled: Boolean) {
        _ztmeEnabled.value = enabled
    }

    fun setQoSmeshEnabled(enabled: Boolean) {
        _qoSmeshEnabled.value = enabled
    }

    fun setAmbientHubEnabled(enabled: Boolean) {
        _ambientHubEnabled.value = enabled
    }
    
    fun setWhisperSyncEnabled(enabled: Boolean) {
        _whisperSyncEnabled.value = enabled
    }

    // Modern setters for physical hardware optimizations
    fun setForceHardwareDecoding(enabled: Boolean) {
        _forceHardwareDecoding.value = enabled
    }

    fun setMediaCodecTunneling(enabled: Boolean) {
        _mediaCodecTunneling.value = enabled
    }

    fun setAudioPassthroughEnabled(enabled: Boolean) {
        _audioPassthroughEnabled.value = enabled
    }

    fun setHdrConversionMode(mode: String) {
        _hdrConversionMode.value = mode
    }

    fun setUsbPerformanceBuffering(enabled: Boolean) {
        _usbPerformanceBuffering.value = enabled
    }

    fun setBypassSslVerification(enabled: Boolean) {
        _bypassSslVerification.value = enabled
    }

    fun setWidevineDrmLevel(level: String) {
        _widevineDrmLevel.value = level
    }

    fun setCecPowerSyncEnabled(enabled: Boolean) {
        _cecPowerSyncEnabled.value = enabled
    }
}

