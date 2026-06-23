package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DiagnosticResult
import com.example.data.repository.DiagnosticService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DiagnosticScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    
    val coroutineScope = rememberCoroutineScope()
    val diagnosticService = remember { DiagnosticService() }
    
    var diagnosticState by remember { 
        mutableStateOf(DiagnosticResult(0, 0, 0f, 0, 0f, "Ready to test connectivity", false, 0f)) 
    }
    var isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(56.dp)
    ) {
        Text("Network Diagnostics", style = MaterialTheme.typography.displayMedium, color = TextPrimary)
        Text("Perform enterprise-grade jitter, packet loss, and path latency analysis.", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            // Stats Panel
            Column(modifier = Modifier.weight(1f)) {
                MetricRow("Ping (Latency)", "${diagnosticState.serverPingMs} ms")
                MetricRow("Jitter", "${diagnosticState.jitterMs} ms")
                MetricRow("Packet Loss", String.format("%.1f %%", diagnosticState.packetLossPercent))
                MetricRow("Path Latency", "${diagnosticState.pathLatencyMs} ms")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                LinearProgressIndicator(
                    progress = { diagnosticState.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = PrimaryColor,
                    trackColor = GlassSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(diagnosticState.statusText, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (!isRunning) {
                    PrimaryTvButton(
                        text = if (diagnosticState.isComplete) "Run Again" else "Start Diagnostics",
                        onClick = {
                            if (!isRunning) {
                                isRunning = true
                                coroutineScope.launch {
                                    diagnosticService.runDiagnostics("edge-cdn.provider.com").collect { state ->
                                        diagnosticState = state
                                    }
                                    isRunning = false
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryTvButton(
                    text = "Back to Settings",
                    onClick = onBack
                )
            }
            
            // Score visualization
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(SurfaceColor, RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Stream Health Score", color = TextSecondary, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val scoreColor = when {
                        diagnosticState.healthScore > 85 -> SuccessColor
                        diagnosticState.healthScore > 65 -> Color(0xFFFFC107) // Amber
                        else -> ErrorColor
                    }
                    
                    if (diagnosticState.isComplete) {
                        Text(
                            text = String.format("%.0f", diagnosticState.healthScore),
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 120.sp,
                            color = scoreColor,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (isRunning) {
                        CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.size(120.dp))
                    } else {
                        Text(
                            text = "--",
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 120.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.titleMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = GlassSurface)
}
