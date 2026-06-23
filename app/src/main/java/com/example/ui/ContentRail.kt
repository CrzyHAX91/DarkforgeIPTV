package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Video
import com.example.ui.theme.TextPrimary

@Composable
fun ContentRail(title: String, videos: List<Video>, onClick: () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 56.dp, vertical = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 56.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(videos) { video ->
                PosterCard(
                    title = video.title,
                    subtitle = if (video.epgProgram != null) { 
                        "Live: ${video.epgProgram.title} (${video.epgProgram.startTime} - ${video.epgProgram.endTime})" 
                    } else { 
                        "Stream"
                    },
                    imageUrl = video.posterUrl,
                    onClick = onClick
                )
            }
        }
    }
}

