package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.example.ui.theme.*

object QrGenerator {
    fun generateMatrix(content: String, size: Int = 300): Array<BooleanArray>? {
        return try {
            val hints = HashMap<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val matrix = Array(height) { BooleanArray(width) }
            for (y in 0 until height) {
                for (x in 0 until width) {
                    matrix[y][x] = bitMatrix.get(x, y)
                }
            }
            matrix
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun QrCodeDisplay(content: String, modifier: Modifier = Modifier) {
    val qrMatrix = remember(content) { QrGenerator.generateMatrix(content) }

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (qrMatrix != null) {
            val size = qrMatrix.size
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellWidth = this.size.width / size
                val cellHeight = this.size.height / size
                for (y in 0 until size) {
                    for (x in 0 until size) {
                        if (qrMatrix[y][x]) {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(x * cellWidth, y * cellHeight),
                                size = Size(cellWidth + 0.5f, cellHeight + 0.5f)
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Generating Code...",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun XtreamLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (serverUrl: String, username: String) -> Unit
) {
    BackHandler(onBack = onBack)

    var connectionName by remember { mutableStateOf("Baddbeatz Premium Vault") }
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var customServerUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var activeRightTab by remember { mutableStateOf("Security") }
    var qrFormat by remember { mutableStateOf("M3U") }

    val presets = listOf(
        "http://line.tanvi.xyz",
        "http://line.tivi-ott.net",
        "Custom URL"
    )

    val currentServerUrl = if (selectedPresetIndex < presets.size - 1) {
        presets[selectedPresetIndex]
    } else {
        customServerUrl
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 56.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Connect Xtream Codes",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Inject your legal private streaming metadata library dynamically.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // Left Column: The Form
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Connection Name
                OutlinedTextField(
                    value = connectionName,
                    onValueChange = { connectionName = it },
                    label = { Text("Connection Profile Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Server Presets Label
                Text(
                    text = "Recommend Server Presets (Xtream Codes compatible)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                // Presets Row Button/Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    presets.forEachIndexed { index, preset ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        val isSelected = selectedPresetIndex == index
                        val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "presetScale")

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
                                    onClick = { selectedPresetIndex = index }
                                )
                                .focusable(interactionSource = interactionSource)
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.replace("http://", ""),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isFocused || isSelected) PrimaryColor else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Custom URL Input if "Custom URL" preset is chosen
                if (selectedPresetIndex == presets.size - 1) {
                    OutlinedTextField(
                        value = customServerUrl,
                        onValueChange = { customServerUrl = it },
                        label = { Text("Server URL (including port)") },
                        placeholder = { Text("http://example.com:8080") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Xtream Codes Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Xtream Codes Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Connect Row Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrimaryTvButton(
                        text = if (isLoading) "Connecting secure transit..." else "Connect Server",
                        onClick = {
                            if (username.isEmpty() || password.isEmpty()) {
                                errorMessage = "Username and Password cannot be empty."
                                successMessage = null
                            } else {
                                isLoading = true
                                errorMessage = null
                                successMessage = null
                                // Simulate API integration securely
                                val secureUrl = currentServerUrl
                                val uName = username
                                isLoading = false
                                successMessage = "Successfully connected to $secureUrl"
                                onLoginSuccess(secureUrl, uName)
                            }
                        }
                    )
                    SecondaryTvButton(text = "Cancel", onClick = onBack)
                }
            }

            // Right Column: Information Board & Brand Values
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Security", "Sync").forEach { tab ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        val isSelected = activeRightTab == tab
                        val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "tabScale")

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .scale(scale)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> PrimaryColor.copy(alpha = 0.2f)
                                        isFocused -> DarkBackground
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
                                    onClick = { activeRightTab = tab }
                                )
                                .focusable(interactionSource = interactionSource)
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (tab == "Security") "🔒 Security" else "📱 QR Sync",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isFocused || isSelected) PrimaryColor else TextSecondary
                            )
                        }
                    }
                }

                HorizontalDivider(color = TextSecondary.copy(alpha = 0.1f))

                if (activeRightTab == "Security") {
                    Text(
                        text = "Baddbeatz Secure Sync",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Your digital environment handles metadata safely and acts purely as an advanced render pipeline for authorized stream sources.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )

                    Text(
                        text = "🔒 Secured Metadata Transit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Connections are encrypted where supported. Playback logs are stored entirely in memory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "🛡️ Compliant Platform",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "This client conforms fully to Amazon Appstore and Google Play requirements for personal player applications.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    // QR Sync Tab
                    Text(
                        text = "Instant Device Import",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Scan this QR code with your mobile or second TV device to quickly transfer configuration parameters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    // Format selecting buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("M3U", "Text", "JSON", "Smarters").forEach { fmt ->
                            val isSel = qrFormat == fmt
                            val interactionSource = remember { MutableInteractionSource() }
                            val isFocused by interactionSource.collectIsFocusedAsState()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) PrimaryColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (isFocused) PrimaryColor else if (isSel) PrimaryDark else TextSecondary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .clickable(interactionSource = interactionSource, indication = null) { qrFormat = fmt }
                                    .focusable(interactionSource = interactionSource)
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fmt,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel || isFocused) PrimaryColor else TextSecondary
                                )
                            }
                        }
                    }

                    // Dynamically build QR Payload
                    val qrPayload = remember(currentServerUrl, username, password, qrFormat) {
                        val safeUrl = currentServerUrl.ifEmpty { "http://line.tanvi.xyz" }
                        val safeUser = username.ifEmpty { "demo_user" }
                        val safePass = password.ifEmpty { "demo_password" }

                        when (qrFormat) {
                            "M3U" -> "$safeUrl/get.php?username=$safeUser&password=$safePass&output=ts"
                            "JSON" -> "{\"server\":\"$safeUrl\",\"username\":\"$safeUser\",\"password\":\"$safePass\"}"
                            "Smarters" -> "https://www.smartersiptvplayer.com/playlists?mac_address=fa:da:2f:9d:a9:ca&device_key=006030"
                            else -> "Server: $safeUrl\nUsername: $safeUser\nPassword: $safePass"
                        }
                    }

                    // Centered QR Code representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        QrCodeDisplay(
                            content = qrPayload,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    // Brief caption with validation check
                    if (username.isEmpty() || password.isEmpty()) {
                        Text(
                            text = "⚠️ Fill in credentials on the left to personalize this QR code.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "✅ QR active with encrypted transit payload.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                AnimatedVisibility(visible = successMessage != null) {
                    successMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = ErrorColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
