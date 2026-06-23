package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.ui.theme.*

import androidx.compose.runtime.collectAsState
import com.example.data.repository.MockMediaRepository
import com.example.data.model.Category

@Composable
fun HomeScreen(
    isXtreamConnected: Boolean,
    connectedServerUrl: String,
    connectedUser: String,
    onNavigateToDetails: () -> Unit,
    onNavigateToExternal: () -> Unit,
    onNavigateToOffline: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToXtream: () -> Unit,
    onNavigateToManual: () -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToAmbient: () -> Unit
) {
    val repository = remember { MockMediaRepository() }
    val recommendations by repository.getRecommendations().collectAsState(initial = emptyList())

    val categories = if (isXtreamConnected) {
        listOf("Home", "Live TV", "Movies", "Series", "Setup Manual", "Recommended Apps", "Ambient Hub", "Settings")
    } else {
        listOf("Home", "Xtream Setup", "Setup Manual", "Recommended Apps", "Ambient Hub", "Settings")
    }
    var selectedCategory by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(categories.size) {
        if (selectedCategory >= categories.size) {
            selectedCategory = 0
        }
    }

    val currentTabName = categories.getOrNull(selectedCategory) ?: "Home"
    
    val filteredRecommendations = remember(recommendations, searchQuery) {
        if (searchQuery.isBlank()) {
            recommendations
        } else {
            recommendations.mapNotNull { category ->
                val filteredVideos = category.videos.filter { video ->
                    video.title.contains(searchQuery, ignoreCase = true) || category.title.contains(searchQuery, ignoreCase = true)
                }
                if (filteredVideos.isNotEmpty()) {
                    category.copy(videos = filteredVideos)
                } else null
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopCategoryTabs(
            categories = categories,
            selectedCategoryIndex = selectedCategory,
            onCategorySelected = { index ->
                val name = categories[index]
                if (name == "Xtream Setup") {
                    onNavigateToXtream()
                } else if (name == "Setup Manual") {
                    onNavigateToManual()
                } else if (name == "Recommended Apps") {
                    onNavigateToApps()
                } else if (name == "Ambient Hub") {
                    onNavigateToAmbient()
                } else if (name == "Settings") {
                    onNavigateToSettings()
                } else {
                    selectedCategory = index
                }
            }
        )
        
        // Global Search Bar
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 56.dp, vertical = 16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search streams or categories...", color = TextSecondary) },
                singleLine = true,
                leadingIcon = {
                    Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PrimaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(start = 56.dp, end = 56.dp, bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (currentTabName == "Home" || !isXtreamConnected) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HeroBanner(
                        title = if (isXtreamConnected) "Baddbeatz Connected" else "Baddbeatz Media",
                        description = if (isXtreamConnected) {
                            "Successfully connected to premium gateway: $connectedServerUrl (User: $connectedUser). Fully isolated & encrypted data tunneling active."
                        } else {
                            "Your premium, secure environment for playing and managing personal, legal media. Connect via Xtream Codes above."
                        },
                        modifier = Modifier.padding(bottom = 32.dp),
                        onWatchClick = {
                            if (isXtreamConnected) onNavigateToDetails() else onNavigateToXtream()
                        },
                        onDetailsClick = onNavigateToDetails
                    )
                }

                if (isXtreamConnected) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DashboardMetricsRow(
                            serverUrl = connectedServerUrl, 
                            user = connectedUser,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                }
                
                // Map all parsed recommendation categories
                filteredRecommendations.forEach { category ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(category.videos) { video ->
                        PosterCard(
                            title = video.title,
                            subtitle = if (video.epgProgram != null) { 
                                "Live: ${video.epgProgram.title} (${video.epgProgram.startTime} - ${video.epgProgram.endTime})" 
                            } else { 
                                "Stream"
                            },
                            imageUrl = video.posterUrl,
                            onClick = onNavigateToDetails
                        )
                    }
                }
                
                if (filteredRecommendations.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        // Show a shimmer content rail while loading recommendations
                        SkeletonContentRail()
                    }
                }
                
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        PrimaryTvButton("VPN Status", onClick = onNavigateToPrivacy)
                        SecondaryTvButton("External Player", onClick = onNavigateToExternal)
                    }
                }
            } else if (currentTabName == "Live TV") {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Text("Live TV Channels", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
                        Text("Sourced from gateway: $connectedServerUrl", style = MaterialTheme.typography.bodyLarge, color = PrimaryColor)
                    }
                }
                filteredRecommendations.filter { it.title.contains("Live", ignoreCase = true) }.forEach { category ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(category.videos) { video ->
                        PosterCard(
                            title = video.title,
                            subtitle = if (video.epgProgram != null) { 
                                "Live: ${video.epgProgram.title} (${video.epgProgram.startTime} - ${video.epgProgram.endTime})" 
                            } else { 
                                "Stream"
                            },
                            imageUrl = video.posterUrl,
                            onClick = onNavigateToDetails
                        )
                    }
                }
            } else if (currentTabName == "Movies") {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Text("Movies & Cinema (VOD)", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
                        Text("Secure private library metadata loaded successfully.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    }
                }
                filteredRecommendations.filter { it.title.contains("VOD", ignoreCase = true) || it.title.contains("Movie", ignoreCase = true) }.forEach { category ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(category.videos) { video ->
                        PosterCard(
                            title = video.title,
                            subtitle = "Stream",
                            imageUrl = video.posterUrl,
                            onClick = onNavigateToDetails
                        )
                    }
                }
            } else if (currentTabName == "Series") {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Text("Series & Shows", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
                        Text("Synchronized series lists for playback.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    }
                }
                filteredRecommendations.filter { it.title.contains("Series", ignoreCase = true) }.forEach { category ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(category.videos) { video ->
                        PosterCard(
                            title = video.title,
                            subtitle = "Stream",
                            imageUrl = video.posterUrl,
                            onClick = onNavigateToDetails
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DetailsScreen(onBack: () -> Unit, onPlay: () -> Unit) {
    BackHandler(onBack = onBack)
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Mock large backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceColor)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(56.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sample Movie Title", style = MaterialTheme.typography.displayLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("2024 • 2h 15m • 4K HDR", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "A cinematic masterpiece providing the best legal viewing experience on your Fire TV.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PrimaryTvButton("Play Now", onClick = onPlay)
                SecondaryTvButton("Download", onClick = { })
            }
        }
    }
}

@Composable
fun PlayerScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        FireOsVideoPlayer(
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun OfflineLibraryScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    
    Column(modifier = Modifier.fillMaxSize().padding(56.dp)) {
        Text("Offline Library", style = MaterialTheme.typography.displayMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your downloaded legal media. Stored securely on device private storage.", color = TextSecondary)
        
        Spacer(modifier = Modifier.height(48.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(SurfaceColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("No downloads yet.", color = TextSecondary, style = MaterialTheme.typography.titleLarge)
        }
    }
}



@Composable
fun ExternalPlayerScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    
    Column(modifier = Modifier.fillMaxSize().padding(56.dp), verticalArrangement = Arrangement.Center) {
        Text("External Player Integration", style = MaterialTheme.typography.displayMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Safe routing to Kodi or VLC for legal network streams.", color = TextPrimary)
        Spacer(modifier = Modifier.height(48.dp))
        PrimaryTvButton("Launch Kodi", onClick = { })
    }
}

