package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AmbientScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentTime))
    val dateString = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date(currentTime))

    // Infinite transition for ambient gentle movements
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    
    val offsetX1 by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x1"
    )
    val offsetY1 by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y1"
    )

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // True black for OLED
            .clickable { onBack() }
    ) {
        // Subtle ambient moving orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            scale(scale = breathingScale) {
                drawCircle(
                    color = Color(0xFF6200EE).copy(alpha = 0.05f),
                    radius = size.width * 0.4f,
                    center = Offset(size.width * 0.5f + offsetX1, size.height * 0.5f + offsetY1)
                )
                drawCircle(
                    color = Color(0xFF03DAC6).copy(alpha = 0.03f),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.3f - offsetX1, size.height * 0.7f - offsetY1)
                )
            }
        }

        // Clock that slowly drifts to prevent burn-in
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp)
                .offset(x = offsetX1.dp, y = offsetY1.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeString,
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = dateString,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.5f)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
                // Tagline indicator that this is OLED safe
                Text(
                    text = "OLED Defender Active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03DAC6).copy(alpha = 0.3f)
                )
            }
        }
    }
}
