package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class TutorialStep(
    val title: String,
    val description: String,
    val note: String,
    val icon: ImageVector,
    val prompt: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun XtreamInstructionScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    var activeStep by remember { mutableIntStateOf(0) }
    var selectedAppCategory by remember { mutableStateOf("Smart TV") }

    val steps = when (selectedAppCategory) {
        "Smart TV" -> listOf(
            TutorialStep(
                title = "Step 1: Obtain a Compatible IPTV Client",
                description = "Download and install an app from your TV's App Store:\n" +
                        "• IPTV Smarters Pro: An excellent, free app for most Smart TVs.\n" +
                        "• IBO Player / IBO Pro Player: A reliable alternative with a clear interface.\n" +
                        "• Hot IPTV / Hot Player Pro: A powerful, flexible choice for virtually all platforms.",
                note = "Ensure the client is downloaded from official secure provider links.",
                icon = Icons.Default.PlayArrow,
                prompt = "Install your preferred application and open it on your device to proceed."
            ),
            TutorialStep(
                title = "Step 2: Choose Login via Xtream API",
                description = "Upon launching your player, navigate to 'ADD NEW USER' or 'Login with Xtream Codes API' or 'Add Playlist via API'.",
                note = "Xtream Codes API securely loads metadata faster than standalone M3U links.",
                icon = Icons.Default.Settings,
                prompt = "Select the API connection option to open the credentials entry form."
            ),
            TutorialStep(
                title = "Step 3: Enter Credentials & Server URL",
                description = "Fill in the form precisely:\n" +
                        "• Name: Baddbeatz Media\n" +
                        "• Username: [Your personal username]\n" +
                        "• Password: [Your personal password]\n" +
                        "• Server URL: http://line.tivi-ott.net",
                note = "Check your platform's specific port if http://line.tivi-ott.net does not resolve.",
                icon = Icons.Default.Info,
                prompt = "Carefully double check the URL syntax before saving."
            ),
            TutorialStep(
                title = "Step 4: Load and Stream Media",
                description = "Click on 'ADD USER' or 'SAVE'. The client will connect to the metadata pipeline and download your active live, movies, and series list indexes.",
                note = "First-time synchronization can take 10-60 seconds depending on stream size.",
                icon = Icons.Default.Warning,
                prompt = "Enjoy your premium, legal media workspace in glorious high definition!"
            )
        )
        "Android" -> listOf(
            TutorialStep(
                title = "Step 1: Obtain a Compatible Android Client",
                description = "Download and install an app from the Google Play Store on your Android TV or Phone:\n" +
                        "• Televizo: A great, free app if you watch via Android TVs or phones.\n" +
                        "• XP IPTV: A very clear and easy app for all Android devices.\n" +
                        "• 9XStream: An excellent, free app option for your Android device.",
                note = "Ensure the client is legal and up to date.",
                icon = Icons.Default.PlayArrow,
                prompt = "Install your preferred application and open it on your device to proceed."
            ),
            TutorialStep(
                title = "Step 2: Choose Login via Xtream API",
                description = "Navigate to settings or the playlist creation screen, and select 'Xtream Codes API' to add your subscription.",
                note = "Xtream API handles categories and EPG automatically.",
                icon = Icons.Default.Settings,
                prompt = "Select the API connection option to open the credentials entry form."
            ),
            TutorialStep(
                title = "Step 3: Enter Credentials & Server URL",
                description = "Fill in the form precisely:\n" +
                        "• Name: Baddbeatz Media\n" +
                        "• Username: [Your personal username]\n" +
                        "• Password: [Your personal password]\n" +
                        "• Server URL: http://line.tanvi.xyz",
                note = "Recommended URL for Android ecosystem apps: http://line.tanvi.xyz",
                icon = Icons.Default.Info,
                prompt = "Carefully double check the port number and syntax before saving."
            ),
            TutorialStep(
                title = "Step 4: Load and Stream Media",
                description = "Save your playlist. The app will sync VOD covers, channels, and Electronic Program Guides (EPG).",
                note = "First-time synchronization can take 10-60 seconds.",
                icon = Icons.Default.Warning,
                prompt = "Enjoy your premium media workspace!"
            )
        )
        "Apple" -> listOf(
            TutorialStep(
                title = "Step 1: Obtain a Compatible iOS/Apple TV Client",
                description = "Download from the App Store:\n" +
                        "• Smarters Player Lite: The ideal choice for anyone with an iPhone or iPad.\n" +
                        "• IPTVX: An advanced player with extra premium visual features for Apple TV.\n" +
                        "• UHF App: An app that is highly recommended specifically for iOS and Apple TV.",
                note = "Apple apps strictly verify APIs, so ensure your credentials are typed exactly.",
                icon = Icons.Default.PlayArrow,
                prompt = "Install the application and open it on your Apple device."
            ),
            TutorialStep(
                title = "Step 2: Choose Login via Xtream API",
                description = "Upon launching the application, select 'Xtream API' or 'Add specific Xtream subscription'.",
                note = "Do not select 'M3U Link' as Xtream Codes API loads much faster with rich layout metadata.",
                icon = Icons.Default.Settings,
                prompt = "Select the API connection option to open the credentials entry form."
            ),
            TutorialStep(
                title = "Step 3: Enter Credentials & Server URL",
                description = "Fill in the form precisely:\n" +
                        "• Name: Baddbeatz Media\n" +
                        "• Username: [Your personal username]\n" +
                        "• Password: [Your personal password]\n" +
                        "• Server URL: http://line.tivi-ott.net",
                note = "If using IPTVX, ensure 'EPG Shift' is correctly timed if applicable.",
                icon = Icons.Default.Info,
                prompt = "Carefully double check the syntax before saving."
            ),
            TutorialStep(
                title = "Step 4: Load and Stream Media",
                description = "Click to save. The application will synchronize your media with the Apple native player UI.",
                note = "Synchronization depends on your Wi-Fi or cellular speed.",
                icon = Icons.Default.Warning,
                prompt = "Enjoy your media workspace in glorious high definition!"
            )
        )
        else -> listOf(
            TutorialStep(
                title = "Step 1: Allow Apps from Unknown Sources",
                description = "Enable installation of performance-optimized wrappers or testing utility versions:\n" +
                        "1. Navigate to Settings on the Fire TV Home Screen.\n" +
                        "2. Select 'My Fire TV' > 'Developer Options'.\n" +
                        "3. Change 'Apps from Unknown Sources' to ON.\n" +
                        "• If Developer Options are not visible, go to Settings > My Fire TV > About, and click the Fire TV device name 7 times rapidly.",
                note = "This toggle is critical for sideloading dedicated smart IPTV players or media tools securely.",
                icon = Icons.Default.Settings,
                prompt = "Enable this preference temporarily to register custom or enterprise builds of major players."
            ),
            TutorialStep(
                title = "Step 2: Clear Application Cache",
                description = "Prevent slow decoding, caching issues, or video stuttering:\n" +
                        "1. Navigate to Fire TV Settings > 'Applications'.\n" +
                        "2. Select 'Manage Installed Applications'.\n" +
                        "3. Find your preferred player and highlight it.\n" +
                        "4. Click 'Clear Cache'. Do NOT select Clear Data unless you need to wipe your login credentials.",
                note = "Clearing cache regularly resolves player-level hardware memory fragmentation.",
                icon = Icons.Default.PlayArrow,
                prompt = "Keep playback fluids by performing cache clearance weekly."
            ),
            TutorialStep(
                title = "Step 3: Leverage Hardware Acceleration",
                description = "Configure the media decoding engine to utilize Fire OS optimized hardware pipelines:\n" +
                        "1. Open your player's Settings.\n" +
                        "2. Select 'Stream Format Setup' or 'Decoder Options'.\n" +
                        "3. Switch player engine defaults to 'Hardware Decoder' / 'HW+' decoder.\n" +
                        "4. Choose 'HLS' (.m3u8) stream format.",
                note = "HW+ decoder uses the built-in GPU to process UHD streams, lowering device temperature and packet drops.",
                icon = Icons.Default.Info,
                prompt = "Restart your OS device to ensure the decoder changes take clean effect."
            ),
            TutorialStep(
                title = "Step 4: Optimize Connectivity",
                description = "Increase streaming buffer capabilities:\n" +
                        "1. Connect the TV device to a 5GHz Wi-Fi band or use a wired ethernet adapter.\n" +
                        "2. Turn off 'Collect App Usage Data' in Settings to maximize background CPU performance.\n" +
                        "3. If using VPN, configure OpenVPN protocol over UDP or switch to the faster WireGuard configuration.",
                note = "FHD/4K stream transit requires a continuous feed of 25+ Mbps for pristine rendering.",
                icon = Icons.Default.Warning,
                prompt = "A lightweight system configuration maximizes uninterrupted frame rates."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 56.dp, vertical = 32.dp)
    ) {
        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Xtream Configuration Manual",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Setup guides for external IPTV Players and Smart TVs.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            // Close button focusable
            ActionButton(
                text = "Back to Home",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // Left Column - Selection of Player type and Steps checklist
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Platform / Ecosystem",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf("Smart TV", "Android", "Apple", "Optimization").forEach { cat ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        val isSelected = selectedAppCategory == cat
                        val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "appScale")

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .scale(scale)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> PrimaryColor.copy(alpha = 0.2f)
                                        isFocused -> SurfaceColor
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (isFocused || isSelected) 2.dp else 1.dp,
                                    color = when {
                                        isFocused -> PrimaryColor
                                        isSelected -> PrimaryDark
                                        else -> TextSecondary.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        selectedAppCategory = cat
                                        activeStep = 0 // Reset steps when switching apps
                                    }
                                )
                                .focusable(interactionSource = interactionSource)
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isFocused || isSelected) PrimaryColor else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Steps list selection (all focusable)
                Text(
                    text = "Instruction Steps",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                steps.forEachIndexed { index, step ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isFocused by interactionSource.collectIsFocusedAsState()
                    val isActive = activeStep == index
                    val scale by animateFloatAsState(targetValue = if (isFocused) 1.02f else 1.0f, label = "stepScale")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) SurfaceColor else if (isFocused) SurfaceColor.copy(alpha = 0.5f) else Color.Transparent)
                            .border(
                                width = if (isFocused || isActive) 2.dp else 0.dp,
                                color = if (isFocused) PrimaryColor else if (isActive) PrimaryDark else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { activeStep = index }
                            )
                            .focusable(interactionSource = interactionSource)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isActive) PrimaryColor else SurfaceColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) DarkBackground else TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = step.title.substringBefore(":"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) PrimaryColor else TextSecondary
                            )
                        }
                    }
                }
            }

            // Right Column - Focused Tutorial Content detailing recommendations
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedContent(
                    targetState = steps[activeStep],
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "stepAnimation"
                ) { currentStep ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = currentStep.icon,
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = currentStep.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        androidx.compose.material3.HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

                        Text(
                            text = currentStep.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            lineHeight = 24.sp
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Recommended Info",
                                    tint = PrimaryDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = currentStep.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Navigation steps at bottom
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (activeStep > 0) {
                                PrimaryTvButton(
                                    text = "< Previous",
                                    onClick = { activeStep-- }
                                )
                            } else {
                                Box(modifier = Modifier.width(100.dp))
                            }

                            Text(
                                text = "Step ${activeStep + 1} of 4",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            if (activeStep < steps.size - 1) {
                                PrimaryTvButton(
                                    text = "Next Step >",
                                    onClick = { activeStep++ }
                                )
                            } else {
                                PrimaryTvButton(
                                    text = "Ready!",
                                    onClick = onBack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "buttonScale")

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) PrimaryColor else SurfaceColor)
            .border(1.dp, if (isFocused) Color.Transparent else TextSecondary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused) DarkBackground else TextPrimary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isFocused) DarkBackground else TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
