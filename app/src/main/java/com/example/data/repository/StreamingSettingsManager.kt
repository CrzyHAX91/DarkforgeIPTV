package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StreamingProfile {
    HIGH_FIDELITY,
    LOW_LATENCY
}

object StreamingSettingsManager {
    private val _currentProfile = MutableStateFlow(StreamingProfile.HIGH_FIDELITY)
    val currentProfile: StateFlow<StreamingProfile> = _currentProfile.asStateFlow()
    
    fun setProfile(profile: StreamingProfile) {
        _currentProfile.value = profile
    }
}
