package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.VpnConnectionStatus
import com.example.data.model.VpnState
import com.example.data.repository.MockVpnStatusRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PrivacySettingsScreen(
    onBack: () -> Unit,
    repository: MockVpnStatusRepository = remember { MockVpnStatusRepository() }
) {
    BackHandler(onBack = onBack)
    
    val coroutineScope = rememberCoroutineScope()
    val vpnStatus by repository.connectionStatus.collectAsState()
    val availableServers by repository.getAvailableServers().collectAsState(initial = emptyList())
    
    var hasConsented by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    
    val isConnected = vpnStatus.state == VpnState.CONNECTED
    val isConnecting = vpnStatus.state == VpnState.CONNECTING
    
    if (showPrivacyPolicy) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyPolicy = false })
    }
    
    if (!hasConsented) {
        ConsentScreen(
            onAccept = { hasConsented = true },
            onDecline = onBack,
            onReadPolicy = { showPrivacyPolicy = true }
        )
    } else {
        Row(modifier = Modifier.fillMaxSize().padding(56.dp)) {
            // Left Column: Controls & Info
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "Privacy Shield",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Provider-agnostic VPN interface. Secure your legal media traffic with zero logging.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    PrimaryTvButton(
                        text = when (vpnStatus.state) {
                            VpnState.CONNECTED -> "Disconnect Privacy Shield"
                            VpnState.CONNECTING -> "Establishing Secure Tunnel..."
                            else -> "Enable Privacy Shield"
                        },
                        onClick = {
                            coroutineScope.launch {
                                if (isConnected || isConnecting) {
                                    repository.disconnect()
                                } else {
                                    val server = availableServers.firstOrNull { it.isRecommended } ?: availableServers.firstOrNull()
                                    if (server != null) {
                                        repository.connect(server)
                                    }
                                }
                            }
                        }
                    )
                    
                    SecondaryTvButton(
                        text = "View Privacy Policy",
                        onClick = { showPrivacyPolicy = true }
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Status Dashboard
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(SurfaceColor, RoundedCornerShape(16.dp))
                        .padding(32.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isConnected) Icons.Default.Lock else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isConnected) PrimaryColor else ErrorColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Tunnel Status: ${vpnStatus.state.name}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                        }
                        
                        if (isConnected) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Active Node: ${vpnStatus.currentServer?.city}, ${vpnStatus.currentServer?.country}", color = TextSecondary)
                            Text("Encryption: AES-256-GCM (Next-Gen AI Verified)", color = PrimaryColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Mocking an innovative "Smart Split Tunnel" state
                            Text("Smart Split Tunnel: Active", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Only media traffic is routed. Background platform traffic flows locally.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        } else if (!isConnecting) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your traffic is utilizing the default ISP route.", color = ErrorColor)
                        }
                    }
                }
            }
            
            // Right Column: Visualization / AI Network Map mock
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight()
                    .background(SurfaceColor, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    if (isConnected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Secure",
                            tint = PrimaryColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Connection Secure", style = MaterialTheme.typography.headlineLarge, color = PrimaryColor)
                        Text("Zero logs · Strict consent · Provider agnostic", color = TextSecondary)
                    } else if (isConnecting) {
                        CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Negotiating Handshake...", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Standby",
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("System Standby", style = MaterialTheme.typography.headlineLarge, color = TextSecondary)
                        Text("Activate the shield to secure media streams.", color = TextSecondary.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

@Composable
fun ConsentScreen(onAccept: () -> Unit, onDecline: () -> Unit, onReadPolicy: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .background(SurfaceColor, RoundedCornerShape(24.dp))
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Privacy", tint = PrimaryColor, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Privacy Shield Consent", style = MaterialTheme.typography.displaySmall, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Before activating the Privacy Shield (VPN Service), you must provide explicit consent. " +
                       "This service encrypts your media app traffic to protect your privacy. " +
                       "We strictly adhere to a zero-logging policy. Your actual IP and traffic data are never stored.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PrimaryTvButton("I Understand & Accept", onClick = onAccept)
                SecondaryTvButton("Decline", onClick = onDecline)
            }
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = onReadPolicy) {
                Text("Read Full Privacy Policy", color = PrimaryColor)
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    // A simple full-screen overlay for TV policy reading
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(56.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Privacy Policy", style = MaterialTheme.typography.displayMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "1. Data Collection: We do not log or store any network traffic or IP addresses.\n" +
                "2. Purpose: The VPN module is strictly for anonymizing personal, legally acquired media traffic.\n" +
                "3. Consent: You may revoke consent and disable the VPN at any time from this screen.\n" +
                "4. Provider Agnostic: This app does not force you to use any commercial VPN tier.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(48.dp))
            PrimaryTvButton("Close", onClick = onDismiss)
        }
    }
}
