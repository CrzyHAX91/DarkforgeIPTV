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
}
