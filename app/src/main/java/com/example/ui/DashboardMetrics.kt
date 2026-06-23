package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 56.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        MetricCard(
            title = "Network Status",
            value = "ONLINE",
            subtitle = "Ping: 24ms",
            icon = Icons.Default.CheckCircle,
            tint = PrimaryColor,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Security & VPN",
            value = "ENCRYPTED",
            subtitle = "IPVanish Active",
            icon = Icons.Default.Lock,
            tint = AccentColor,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Active Sessions",
            value = "1 / 4",
            subtitle = "User: $user",
            icon = Icons.Default.Refresh,
            tint = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Subscription",
            value = "ACTIVE",
            subtitle = "Quality: 4K HDR",
            icon = Icons.Default.Info,
            tint = PrimaryColor,
            modifier = Modifier.weight(1f)
        )
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
