package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.AccentColor

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(3000) // 3 seconds intro
        onSplashComplete()
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "alphaAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "scaleAnimation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Backlight ambient glow
        Canvas(modifier = Modifier.fillMaxSize().blur(80.dp).alpha(alpha)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PrimaryColor.copy(alpha = 0.15f * pulseGlow), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.width / 2f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            Text(
                text = "Welcome to the",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Light,
                letterSpacing = androidx.compose.ui.unit.TextUnit(3f, androidx.compose.ui.unit.TextUnitType.Sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "New Fire Stream",
                style = MaterialTheme.typography.displayLarge,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            // Premium linear indeterminate indicator
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pulseGlow)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, AccentColor, PrimaryColor, Color.Transparent)
                            )
                        )
                )
            }
        }
    }
}
