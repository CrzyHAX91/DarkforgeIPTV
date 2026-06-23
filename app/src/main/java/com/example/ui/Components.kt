package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PrimaryTvButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "buttonScale")

    Box(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minWidth = 140.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isFocused) {
                    Brush.linearGradient(listOf(PrimaryColor, PrimaryDark))
                } else {
                    Brush.linearGradient(listOf(SurfaceColor, DarkBackground))
                }
            )
            .border(
                width = 1.dp,
                color = if (isFocused) Color.White.copy(alpha = 0.5f) else PrimaryColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused) DarkBackground else PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = if (isFocused) DarkBackground else TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SecondaryTvButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "secondaryButtonScale")

    Box(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minWidth = 140.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isFocused) GlassSurface else Color.Transparent
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) PrimaryColor else TextSecondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused) PrimaryColor else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = if (isFocused) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WideFeatureCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.03f else 1.0f, label = "wideCardScale")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                color = if (isFocused) PrimaryColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DarkBackground.copy(alpha = 0.9f), Color.Transparent),
                        endX = 800f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(32.dp)
                .fillMaxWidth(0.6f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TopCategoryTabs(
    categories: List<String>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 56.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEachIndexed { index, category ->
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()
            val isSelected = index == selectedCategoryIndex
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(interactionSource = interactionSource, indication = null) { onCategorySelected(index) }
                    .focusable(interactionSource = interactionSource)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    color = when {
                        isFocused -> PrimaryColor
                        isSelected -> TextPrimary
                        else -> TextSecondary.copy(alpha = 0.7f)
                    },
                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // Animated indicator
                val indicatorWidth by animateFloatAsState(
                    targetValue = if (isFocused || isSelected) 40f else 0f, 
                    label = "indicatorWidth"
                )
                
                Box(
                    modifier = Modifier
                        .width(indicatorWidth.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(
                            if (isFocused) PrimaryColor else if (isSelected) PrimaryDark else Color.Transparent
                        )
                )
            }
        }
    }
}
