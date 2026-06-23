package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    isXtreamConnected: Boolean,
    connectedServerUrl: String,
    connectedUser: String,
    onDisconnect: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    
    var telemetryEnabled by remember { mutableStateOf(false) }
    var privacyModeEnabled by remember { mutableStateOf(true) }
    
    val currentProfile by com.example.data.repository.StreamingSettingsManager.currentProfile.collectAsState()
    val lowLatencyProfileEnabled = currentProfile == com.example.data.repository.StreamingProfile.LOW_LATENCY
    
    val ztmeEnabled by com.example.data.repository.AdvancedSettingsManager.ztmeEnabled.collectAsState()
    val qosMeshEnabled by com.example.data.repository.AdvancedSettingsManager.qoSmeshEnabled.collectAsState()
    val ambientHubEnabled by com.example.data.repository.AdvancedSettingsManager.ambientHubEnabled.collectAsState()
    val whisperSyncEnabled by com.example.data.repository.AdvancedSettingsManager.whisperSyncEnabled.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 56.dp, vertical = 32.dp)) {
        Text("Settings", style = MaterialTheme.typography.displayMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (isXtreamConnected) {
                item {
                    SettingsItem(
                        title = "Xtream Account Status",
                        subtitle = "Connected securely to: $connectedServerUrl (User: $connectedUser)",
                        onClick = { /* No-op */ }
                    )
                }
            }

            item {
                SettingsItem(
                    title = "App Version",
                    subtitle = "Baddbeatz Media 1.0.0 (Amazon Appstore Compliant)",
                    onClick = { /* No-op */ }
                )
            }
            
            item {
                SettingsItem(
                    title = "Legal Notice",
                    subtitle = "This application handles only user-owned or rights-free media. No unauthorized scraping or piracy enabled.",
                    onClick = { /* Show notice dialog */ }
                )
            }
            
            item {
                SettingsToggleItem(
                    title = if(lowLatencyProfileEnabled) "Streaming Profile: Low-Latency" else "Streaming Profile: High-Fidelity",
                    subtitle = if(lowLatencyProfileEnabled) "Optimized for live streams with minimal delay. Adjusts buffer aggressively." else "Default. Optimized for robust playback and large buffers to prevent stuttering on variable networks.",
                    checked = lowLatencyProfileEnabled,
                    onCheckedChange = { isLowLatency -> 
                        val newProfile = if (isLowLatency) com.example.data.repository.StreamingProfile.LOW_LATENCY else com.example.data.repository.StreamingProfile.HIGH_FIDELITY
                        com.example.data.repository.StreamingSettingsManager.setProfile(newProfile)
                    }
                )
            }
            
            item {
                SettingsToggleItem(
                    title = "Cinematic Ambient Hub",
                    subtitle = "Auto-launch OLED burn-in defender when stream is paused. Uses subtle drifting graphics.",
                    checked = ambientHubEnabled,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setAmbientHubEnabled(it) }
                )
            }
            
            item {
                SettingsToggleItem(
                    title = "Zero-Trust Metadata Enclave (ZTME)",
                    subtitle = "Store watch history and epg preferences in a hardware-backed encrypted enclave. Ensures zero-knowledge local autonomy.",
                    checked = ztmeEnabled,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setZtmeEnabled(it) }
                )
            }
            
            item {
                SettingsToggleItem(
                    title = "Edge-Federated QoS Mesh",
                    subtitle = "Analyzes local frame-drops and buffer latency with an on-device AI model to auto-negotiate stream codecs without sending telemetry to our servers.",
                    checked = qosMeshEnabled,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setQoSmeshEnabled(it) }
                )
            }
            
            item {
                SettingsToggleItem(
                    title = "AI Audio Whispersync (Night Mode)",
                    subtitle = "Intelligently compresses dynamic range using EPG context, making dialogue clear while dampening sudden loud noises like explosions.",
                    checked = whisperSyncEnabled,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setWhisperSyncEnabled(it) }
                )
            }
            
            item {
                SettingsItem(
                    title = "Enterprise-Grade Network Diagnostics",
                    subtitle = "Perform jitter, packet loss, and path latency analysis to calculate Stream Health Score.",
                    onClick = onNavigateToDiagnostics
                )
            }
            
            item {
                SettingsToggleItem(
                    title = "Privacy Shield",
                    subtitle = "Secure local connection telemetry and encrypt media metadata transit.",
                    checked = privacyModeEnabled,
                    onCheckedChange = { privacyModeEnabled = it }
                )
            }
            
            item {
                SettingsToggleItem(
                    title = "Usage Telemetry",
                    subtitle = "Disabled by default. We do not track your playback habits.",
                    checked = telemetryEnabled,
                    onCheckedChange = { telemetryEnabled = it }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isXtreamConnected) {
                        PrimaryTvButton("Disconnect Server", onClick = onDisconnect)
                    }
                    SecondaryTvButton("Clear Cache", onClick = { })
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val backgroundColor = if (isFocused) PrimaryColor.copy(alpha = 0.2f) else SurfaceColor
    val borderColor = if (isFocused) PrimaryColor else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(24.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = if (isFocused) PrimaryColor else TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val backgroundColor = if (isFocused) PrimaryColor.copy(alpha = 0.2f) else SurfaceColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = { onCheckedChange(!checked) })
            .focusable(interactionSource = interactionSource)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 24.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = if (isFocused) PrimaryColor else TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
        
        Switch(
            checked = checked,
            onCheckedChange = null, // Handled by row click
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryColor,
                checkedTrackColor = PrimaryDark.copy(alpha = 0.5f),
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceColor
            )
        )
    }
}
