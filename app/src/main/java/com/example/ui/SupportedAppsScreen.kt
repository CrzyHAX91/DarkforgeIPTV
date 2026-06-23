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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.sp

data class RecommendedAppInfo(
    val name: String,
    val description: String,
    val platform: String,
    val isFavorite: Boolean = false,
    val icon: ImageVector,
    val installationInstructions: String
)

@Composable
fun SupportedAppsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedApp by remember { mutableStateOf<RecommendedAppInfo?>(null) }

    val apps = listOf(
        RecommendedAppInfo("IPTV Smarters Pro", "Our favorite. An excellent, free app for most Smart TVs.", "Smart TV / Android", true, Icons.Default.PlayArrow, "1. Open your TV's App Store.\n2. Search for 'IPTV Smarters Pro' and install.\n3. Open the app and select 'Login with Xtream Codes API'.\n4. Alternatively, manage playlists for this device quickly via their web portal:\nhttps://www.smartersiptvplayer.com/playlists?mac_address=fa:da:2f:9d:a9:ca&device_key=006030"),
        RecommendedAppInfo("Hot IPTV / Pro", "A powerful, flexible choice for virtually all platforms and devices.", "Cross-Platform", false, Icons.Default.CheckCircle, "1. Download 'Hot IPTV' from your device's respective App Store.\n2. Open the app to find your device's MAC Address.\n3. Go to the Hot IPTV website and enter your MAC Address to upload your playlist or use the Xtream login parameters directly if supported."),
        RecommendedAppInfo("IBO Player", "A reliable alternative, especially for users with a Smart TV.", "Smart TV", false, Icons.Default.PlayArrow, "1. Download 'IBO Player' from your TV's App Store.\n2. Open the app to view your Device Mac and Device Key.\n3. Go to the IBO Player website and upload your playlist using the MAC and Key.\n4. Restart the app on your TV."),
        RecommendedAppInfo("Televizo", "A good, free app if you watch via Android TVs or Android phones.", "Android / Fire OS", false, Icons.Default.Info, "1. Open the Google Play Store on your Android device.\n2. Search for 'Televizo' and install it.\n3. Open Televizo, go to Settings -> Playlists.\n4. Click the '+' button and choose 'New Xtream Codes playlist'.\n5. Fill in your credentials and server URL."),
        RecommendedAppInfo("XP IPTV", "A very clear and easy app for all Android devices.", "Android / Fire OS", false, Icons.Default.Info, "1. Download 'XP IPTV' from the Google Play Store.\n2. Open the app.\n3. Select Xtream Codes as the connection method.\n4. Input the provided server credentials."),
        RecommendedAppInfo("9XStream", "An excellent, free app option for your Android device.", "Android", false, Icons.Default.Info, "1. Install '9XStream' from the Google Play Store.\n2. Launch the app and select 'Xtream API Login'.\n3. Provide the name, user, password, and URL to connect to the stream pipeline."),
        RecommendedAppInfo("Smarters Lite", "The ideal choice for anyone with an iPhone or iPad.", "iOS", false, Icons.Default.Info, "1. Open the Apple App Store.\n2. Search for 'Smarters Player Lite' and install.\n3. Tap 'Login with Xtream Codes API'.\n4. Type in your username, password, and portal URL."),
        RecommendedAppInfo("IPTVX", "An advanced player with extra features for Apple TV.", "Apple TV", false, Icons.Default.PlayArrow, "1. Open the App Store on your Apple TV.\n2. Search for 'IPTVX' and download.\n3. Follow the on-screen instructions to add an Xtream Codes API playlist.\n4. Note: Some premium features in IPTVX may require an in-app purchase."),
        RecommendedAppInfo("UHF App", "An application we highly recommend for iOS and Apple TV.", "iOS / Apple TV", true, Icons.Default.CheckCircle, "1. Install 'UHF' from the Apple App Store.\n2. Add a new playlist.\n3. Choose 'Xtream Codes' as the format.\n4. Enter the host/URL, username, and password precisely as instructed."),
        RecommendedAppInfo("IBO Pro Player", "A great app with a very clear and user-friendly interface.", "Smart TV", false, Icons.Default.PlayArrow, "1. Find 'IBO Pro Player' in your smart TV store.\n2. Follow the prompt to get your MAC address.\n3. Register and upload your playlist via the IBO Pro portal site.")
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Text(
                    text = "Recommended IPTV Applications",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "A curated list of supported players segmented by compatible platform.",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps) { app ->
                    AppRecommendationCard(
                        app = app,
                        onClick = { selectedApp = app }
                    )
                }
            }
        }

        // Overlay Modal
        if (selectedApp != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { selectedApp = null }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceColor)
                        .border(1.dp, PrimaryColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* Intercept clicks inside the modal */ }
                        )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedApp!!.name,
                                    style = MaterialTheme.typography.displaySmall,
                                    color = PrimaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedApp!!.platform,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextSecondary
                                )
                            }
                            
                            val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
                            val closeInteractionSource = remember { MutableInteractionSource() }
                            val isCloseFocused by closeInteractionSource.collectIsFocusedAsState()
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCloseFocused) ErrorColor else DarkBackground)
                                    .clickable(interactionSource = closeInteractionSource, indication = null) { selectedApp = null }
                                    .focusable(interactionSource = closeInteractionSource)
                                    .focusRequester(focusRequester)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Close", color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            
                            LaunchedEffect(selectedApp) {
                                focusRequester.requestFocus()
                            }
                        }
                        
                        androidx.compose.material3.HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
                        
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedApp!!.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Installation Instructions",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedApp!!.installationInstructions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppRecommendationCard(
    app: RecommendedAppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "cardScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isFocused) 16.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = PrimaryColor.copy(alpha = glowAlpha)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isFocused) listOf(PrimaryColor, PrimaryDark) else listOf(TextSecondary.copy(alpha = 0.2f), Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(20.dp)
    ) {
        if (isFocused) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryColor.copy(alpha = 0.15f), Color.Transparent),
                                radius = size.width
                            )
                        )
                    }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isFocused) PrimaryColor.copy(alpha = 0.2f) else DarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = app.icon,
                        contentDescription = app.platform,
                        tint = if (isFocused) PrimaryColor else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                if (app.isFavorite) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = AccentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "TOP PICK",
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isFocused) PrimaryColor else TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = app.platform,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryDark,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = app.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                minLines = 3,
                maxLines = 3
            )
        }
    }
}
