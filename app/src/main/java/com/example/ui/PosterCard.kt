package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

@Composable
fun PosterCard(
    title: String,
    subtitle: String? = null,
    imageUrl: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "posterScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "glowAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .shadow(
                    elevation = if (isFocused) 16.dp else 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = PrimaryColor.copy(alpha = glowAlpha)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceColor)
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) PrimaryColor else Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder for image with faint glowing radial grad
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(PrimaryColor.copy(alpha = 0.1f), Color.Transparent),
                                    radius = size.width
                                )
                            )
                        }
                )
            }
            
            // Icon
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = if (isFocused) PrimaryColor else TextSecondary.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(if (isFocused) 48.dp else 40.dp)
                    .align(Alignment.Center)
            )
            
            // Futuristic bottom overlay reflection
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 0.6f
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
            color = if (isFocused) PrimaryColor else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
