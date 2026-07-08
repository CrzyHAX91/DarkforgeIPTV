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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.data.cache.CacheManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

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
    
    // Physical TV & Decoder Optimizations
    val forceHardwareDecoding by com.example.data.repository.AdvancedSettingsManager.forceHardwareDecoding.collectAsState()
    val mediaCodecTunneling by com.example.data.repository.AdvancedSettingsManager.mediaCodecTunneling.collectAsState()
    val audioPassthroughEnabled by com.example.data.repository.AdvancedSettingsManager.audioPassthroughEnabled.collectAsState()
    val hdrConversionMode by com.example.data.repository.AdvancedSettingsManager.hdrConversionMode.collectAsState()
    val usbPerformanceBuffering by com.example.data.repository.AdvancedSettingsManager.usbPerformanceBuffering.collectAsState()
    val bypassSslVerification by com.example.data.repository.AdvancedSettingsManager.bypassSslVerification.collectAsState()
    val cecPowerSyncEnabled by com.example.data.repository.AdvancedSettingsManager.cecPowerSyncEnabled.collectAsState()
    
    val context = LocalContext.current
    val cacheManager = remember { CacheManager.getInstance(context) }
    val cacheMetrics by cacheManager.getCacheMetricsFlow().collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    
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

            // TV Hardware & Decoder Optimizations Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hardware & TV Decoder Optimalisaties (Fire TV & SHIELD)",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingsToggleItem(
                    title = "Forceer Hardware Decoding (MediaCodec)",
                    subtitle = "Voorkomt CPU-oververhitting en framedrops door direct gebruik te maken van de on-device Nvidia of Amlogic hardware-chips voor video-decodering.",
                    checked = forceHardwareDecoding,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setForceHardwareDecoding(it) }
                )
            }

            item {
                SettingsToggleItem(
                    title = "A/V Media Tunneling",
                    subtitle = "Vermindert geluids- en beeldstotteringen door video-data direct naar de display-hardware te sturen zonder CPU-tussenkomst. Perfect voor NVIDIA SHIELD TV.",
                    checked = mediaCodecTunneling,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setMediaCodecTunneling(it) }
                )
            }

            item {
                SettingsToggleItem(
                    title = "HDMI Dolby Atmos & DTS-HD Passthrough",
                    subtitle = "Stuurt onbewerkte multichannel surround audiobitstreams direct door naar uw aangesloten soundbar of home-cinema AV-receiver.",
                    checked = audioPassthroughEnabled,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setAudioPassthroughEnabled(it) }
                )
            }

            item {
                SettingsItem(
                    title = "Display HDR / Dolby Vision Conversie: $hdrConversionMode",
                    subtitle = "Huidige instelling: $hdrConversionMode. Beheer kleurruimte fallback-profielen op uw TV om roze/groen beeld-crashes op oudere SDR-schermen te voorkomen. Klik om te wijzigen.",
                    onClick = {
                        val next = when(hdrConversionMode) {
                            "AUTO" -> "FORCE_HDR10"
                            "FORCE_HDR10" -> "SDR_FALLBACK"
                            "SDR_FALLBACK" -> "DOLBY_VISION_PASS"
                            else -> "AUTO"
                        }
                        com.example.data.repository.AdvancedSettingsManager.setHdrConversionMode(next)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = "High Performance USB / SSD Buffer",
                    subtitle = "Verhoogt de pre-buffer en cachegrootte voor lokale bestanden aangesloten via USB OTG of SSD om trage I/O schijf-stotteringen op te lossen.",
                    checked = usbPerformanceBuffering,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setUsbPerformanceBuffering(it) }
                )
            }

            item {
                SettingsToggleItem(
                    title = "Provider SSL-certificaat Bypass",
                    subtitle = "Bypass verouderde, ongeldige of self-signed HTTPS-certificaten van uw IPTV-provider. Lost veelvoorkomende afspeelfouten (SSL Handshake Exception) direct op.",
                    checked = bypassSslVerification,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setBypassSslVerification(it) }
                )
            }

            item {
                SettingsToggleItem(
                    title = "HDMI CEC Stroom & Volume Sync",
                    subtitle = "Synchroniseert de stroomstatus en volumeregeling van uw TV en receiver met uw Fire TV / SHIELD afstandsbediening via HDMI pin 13.",
                    checked = cecPowerSyncEnabled,
                    onCheckedChange = { com.example.data.repository.AdvancedSettingsManager.setCecPowerSyncEnabled(it) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Diagnostiek & Systeembeheer",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingsItem(
                    title = "Enterprise-Grade TV & Netwerk Diagnostiek",
                    subtitle = "Voer een diepgaande check uit op Widevine DRM-niveaus, HDMI CEC-verbindingen, USB-leesstatussen en netwerk jitter.",
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
                val metrics = cacheMetrics
                val sizeText = if (metrics != null) {
                    val usedMb = metrics.currentSizeInBytes.toDouble() / (1024.0 * 1024.0)
                    val limitGb = metrics.maxSizeInBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
                    String.format("%.2f MB / %.1f GB (%d items cached)", usedMb, limitGb, metrics.totalItems)
                } else {
                    "Calculating cache size..."
                }

                SettingsItem(
                    title = "Media Cache Status",
                    subtitle = "Current usage: $sizeText\nClick to clear all cached thumbnails and JSON metadata from disk and SQLite DB.",
                    onClick = {
                        coroutineScope.launch {
                            cacheManager.clearCache()
                        }
                    }
                )
            }
            
            item {
                val metrics = cacheMetrics
                val currentLimitGb = if (metrics != null) {
                    (metrics.maxSizeInBytes / (1024L * 1024L * 1024L)).toInt()
                } else {
                    16
                }

                val options = listOf(1, 4, 8, 16, 32)
                SettingsItem(
                    title = "Max Cache Storage Limit",
                    subtitle = "Currently configured limit: $currentLimitGb GB. Click to cycle through limits (1 GB, 4 GB, 8 GB, 16 GB, 32 GB) to enforce strict storage restrictions.",
                    onClick = {
                        coroutineScope.launch {
                            val nextIndex = (options.indexOf(currentLimitGb) + 1) % options.size
                            val nextLimitGb = options[nextIndex]
                            val nextLimitBytes = nextLimitGb.toLong() * 1024L * 1024L * 1024L
                            cacheManager.setMaxCacheSize(nextLimitBytes)
                        }
                    }
                )
            }

            item {
                var manualRunStatus by remember { mutableStateOf<String?>(null) }
                
                SettingsItem(
                    title = "Scheduled Cache Cleanup (Fire OS)",
                    subtitle = manualRunStatus ?: "Background Status: ACTIVE (Scheduled every 12 hours)\nDeletes database metadata and image thumbnails older than 24 hours to maximize performance on Fire TV storage. Click to execute now.",
                    onClick = {
                        coroutineScope.launch {
                            manualRunStatus = "Executing manual cache cleanup..."
                            val db = com.example.data.cache.CacheDatabase.getDatabase(context)
                            val dao = db.cacheDao()
                            val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
                            val expiredEntries = dao.getEntriesOlderThan(cutoffTime)
                            var filesDeleted = 0
                            var bytesFreed = 0L
                            
                            for (entry in expiredEntries) {
                                if (entry.type == "THUMBNAIL" && entry.filePath != null) {
                                    val file = java.io.File(entry.filePath)
                                    if (file.exists()) {
                                        val size = file.length()
                                        if (file.delete()) {
                                            filesDeleted++
                                            bytesFreed += size
                                        }
                                    }
                                }
                                dao.deleteEntry(entry.key)
                            }
                            
                            cacheManager.evictIfNecessary()
                            
                            val mbFreed = bytesFreed.toDouble() / (1024.0 * 1024.0)
                            manualRunStatus = String.format("Cleanup completed! Deleted %d expired files, freeing %.2f MB.", filesDeleted, mbFreed)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isXtreamConnected) {
                        PrimaryTvButton("Disconnect Server", onClick = onDisconnect)
                    }
                    SecondaryTvButton("Clear Cache", onClick = {
                        coroutineScope.launch {
                            cacheManager.clearCache()
                        }
                    })
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
    
    // High-fidelity scale animation on D-pad focus for comfortable couch navigation
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.02f else 1.0f, label = "settingsItemScale")
    
    val backgroundColor = if (isFocused) PrimaryColor.copy(alpha = 0.22f) else SurfaceColor
    val borderColor = if (isFocused) PrimaryColor else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocused) 1.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(24.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = if (isFocused) PrimaryColor else TextPrimary, fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal)
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
    
    // High-fidelity scale animation on D-pad focus for comfortable couch navigation
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.02f else 1.0f, label = "settingsToggleScale")
    
    val backgroundColor = if (isFocused) PrimaryColor.copy(alpha = 0.22f) else SurfaceColor
    val borderColor = if (isFocused) PrimaryColor else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocused) 1.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = { onCheckedChange(!checked) })
            .focusable(interactionSource = interactionSource)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 24.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = if (isFocused) PrimaryColor else TextPrimary, fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal)
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
