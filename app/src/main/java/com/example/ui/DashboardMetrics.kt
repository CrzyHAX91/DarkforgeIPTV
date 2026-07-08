package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DashboardMetricsRow(
    serverUrl: String,
    user: String,
    modifier: Modifier = Modifier
) {
    val isVirtual = serverUrl == "http://virtueel-netwerk.local:8080" || serverUrl.contains("virtual") || serverUrl.contains("virtueel")
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 56.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Core KPIs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            MetricCard(
                title = "Netwerk Status",
                value = if (isVirtual) "VIRTUEEL" else "ONLINE",
                subtitle = if (isVirtual) "Lokale Netwerk Sandbox" else "Ping: 24ms (Verbonden)",
                icon = Icons.Default.CheckCircle,
                tint = if (isVirtual) PrimaryColor else SuccessColor,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Beveiliging & Tunnel",
                value = if (isVirtual) "GEÏSOLEERD" else "ENCRYPTED",
                subtitle = if (isVirtual) "Lokaal Gesimuleerde VPN" else "AES-256 IPVanish Actief",
                icon = Icons.Default.Lock,
                tint = if (isVirtual) PrimaryColor else SuccessColor,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Actieve Sessies",
                value = if (isVirtual) "DEMOMODUS" else "1 / 4 SESSiES",
                subtitle = "Gebruiker: $user",
                icon = Icons.Default.Refresh,
                tint = AccentColor,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Abonnement",
                value = if (isVirtual) "LIFETIME" else "ENTERPRISE",
                subtitle = if (isVirtual) "Virtual Ultra HD 4K" else "Verloopt: Onbeperkt",
                icon = Icons.Default.Info,
                tint = PrimaryColor,
                modifier = Modifier.weight(1f)
            )
        }

        // Enterprise Transit and QoS Console (Dutch & English professional specifications)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor.copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Pulsing green/cyan status dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isVirtual) PrimaryColor else SuccessColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ENTERPRISE SECURE TRANSIT & CACHE CONSOLE",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "FIRE OS COMPATIBLE • ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isVirtual) PrimaryColor else SuccessColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Geselecteerde Gateway Node",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isVirtual) "virtueel-netwerk.local:8080 (AMS-EDGE-01)" else serverUrl,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Stream Encapsulatie Protocol",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ZTME (Zero-Trust Media Encapsulation) over SSL",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fire TV Cache Optimalisatie",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Actief (Schoonmaak om de 12 uur ingepland)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.05f))
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isVirtual) {
                            "DEMO METADATA: Actieve virtuele pre-caching techniek simuleert 4K streams zonder belasting van extern dataverbruik. Ideaal voor testomgevingen."
                        } else {
                            "QoS OPTIMALISATIE: Dynamische zero-latency pre-caching is ingeschakeld. Streams starten direct zonder buffertijden."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary.copy(alpha = 0.8f)
            )
        }
    }
}

