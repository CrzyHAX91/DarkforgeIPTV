package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HeroBanner(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onWatchClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(DarkBackground)
            // Add a subtle border glow
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryColor.copy(alpha = 0.5f), Color.Transparent, AccentColor.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        // Futuristic mesh / radial gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(PrimaryColor.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(size.width * 0.8f, 0f),
                            radius = size.width * 0.6f
                        )
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentColor.copy(alpha = 0.1f), Color.Transparent),
                            center = Offset(0f, size.height),
                            radius = size.width * 0.5f
                        )
                    )
                }
        )
        
        // Deep fade at the bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, DarkBackground.copy(alpha = 0.95f)),
                        startY = 300f
                    )
                )
        )

        // Glassmorphic Scrim for text readability left
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DarkBackground.copy(alpha = 0.95f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 56.dp, bottom = 48.dp, end = 24.dp)
                .fillMaxWidth(0.55f)
        ) {
            Text(
                text = "FEATURED PREMIERE",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = AccentColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary.copy(alpha = 0.9f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(36.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                PrimaryTvButton("Stream HD", onClick = onWatchClick)
                SecondaryTvButton("More Details", onClick = onDetailsClick)
            }
        }
    }
}

