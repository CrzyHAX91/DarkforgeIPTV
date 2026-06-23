package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SurfaceColor

@Composable
fun ShimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            SurfaceColor.copy(alpha = 0.6f),
            SurfaceColor.copy(alpha = 0.2f),
            SurfaceColor.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerAnimation"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun SkeletonHeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ShimmerBrush())
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(48.dp)
                .fillMaxWidth(0.6f)
        ) {
            Box(modifier = Modifier.width(300.dp).height(48.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceColor))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceColor))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(24.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceColor))
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.width(140.dp).height(48.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceColor))
                Box(modifier = Modifier.width(140.dp).height(48.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceColor))
            }
        }
    }
}

@Composable
fun SkeletonContentRail() {
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = 56.dp, vertical = 16.dp)
                .width(200.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ShimmerBrush())
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 56.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            userScrollEnabled = false
        ) {
            items(5) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ShimmerBrush())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceColor.copy(alpha=0.5f)))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceColor.copy(alpha=0.5f)))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeLoadingSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(104.dp)) // Approximate TopCategoryTabs height + padding
        
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Box(modifier = Modifier.padding(horizontal = 56.dp)) {
                SkeletonHeroBanner()
            }
            SkeletonContentRail()
            SkeletonContentRail()
        }
    }
}
