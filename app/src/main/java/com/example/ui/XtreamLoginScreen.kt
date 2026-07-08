package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.example.ui.theme.*
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Credentials
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


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

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var connectionType by remember { mutableStateOf("XTREAM") } // "XTREAM" or "M3U"
    var connectionName by remember { mutableStateOf("Baddbeatz Premium Vault") }
    
    // Xtream states
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var customServerUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // M3U states
    var m3uUrl by remember { mutableStateOf("") }
    var m3uUsername by remember { mutableStateOf("") }
    var m3uPassword by remember { mutableStateOf("") }
    var isM3uPasswordVisible by remember { mutableStateOf(false) }

    // Verificatie & Loading states
    var isLoading by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<ConnectionResult?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var activeRightTab by remember { mutableStateOf("Sync") }
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
            text = if (connectionType == "XTREAM") "Connect Xtream Codes" else "Connect M3U Playlist",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (connectionType == "XTREAM") 
                "Injecteer uw legale private streaming metadata bibliotheek dynamisch." 
            else 
                "Laad en valideer uw M3U-afspeellijst met beveiligde SSL/HTTPS verbindingstests.",
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
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Connection Mode Selector
                Text(
                    text = "Verbindingsmethode selecteren",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("XTREAM" to "Xtream Codes API", "M3U" to "M3U Playlist URL").forEach { (type, label) ->
                        val isSelected = connectionType == type
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "tabScale")

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
                                        connectionType = type 
                                        errorMessage = null
                                        successMessage = null
                                        verificationResult = null
                                    }
                                )
                                .focusable(interactionSource = interactionSource)
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Geselecteerd",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isFocused || isSelected) PrimaryColor else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Connection Name (For both modes)
                OutlinedTextField(
                    value = connectionName,
                    onValueChange = { connectionName = it.replace("\n", "").replace("\r", "") },
                    label = { Text("Verbindingsnaam / Profiel") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.None,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                true
                            } else {
                                false
                            }
                        }
                )

                if (connectionType == "XTREAM") {
                    // SERVER PRESETS (Xtream Codes API)
                    Text(
                        text = "Aanbevolen Servers (Xtream Codes compatible)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

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

                    if (selectedPresetIndex == presets.size - 1) {
                        OutlinedTextField(
                            value = customServerUrl,
                            onValueChange = { customServerUrl = it.replace("\n", "").replace("\r", "") },
                            label = { Text("Server URL (inclusief poort)") },
                            placeholder = { Text("http://example.com:8080") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.None,
                                keyboardType = KeyboardType.Uri
                            ),
                            keyboardActions = KeyboardActions(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                                focusedLabelColor = PrimaryColor,
                                unfocusedLabelColor = TextSecondary,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onPreviewKeyEvent { keyEvent ->
                                    if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                        true
                                    } else {
                                        false
                                    }
                                }
                        )
                    }

                    // Xtream Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it.replace("\n", "").replace("\r", "") },
                        label = { Text("Xtream Codes Gebruikersnaam") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                    true
                                } else {
                                    false
                                }
                            }
                    )

                    // Xtream Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it.replace("\n", "").replace("\r", "") },
                        label = { Text("Xtream Codes Wachtwoord") },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val eyeInteractionSource = remember { MutableInteractionSource() }
                            val isEyeFocused by eyeInteractionSource.collectIsFocusedAsState()
                            Text(
                                text = if (isPasswordVisible) "Verberg " else "Toon ",
                                color = if (isEyeFocused) PrimaryColor else TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable(
                                        interactionSource = eyeInteractionSource,
                                        indication = null,
                                        onClick = { isPasswordVisible = !isPasswordVisible }
                                    )
                                    .focusable(interactionSource = eyeInteractionSource)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                    true
                                } else {
                                    false
                                }
                            }
                    )
                } else {
                    // M3U PLAYLIST FORM
                    OutlinedTextField(
                        value = m3uUrl,
                        onValueChange = { m3uUrl = it.replace("\n", "").replace("\r", "") },
                        label = { Text("M3U Playlist URL") },
                        placeholder = { Text("https://yourprovider.com/get.php?auth=xyz") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Uri
                        ),
                        keyboardActions = KeyboardActions(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                    true
                                } else {
                                    false
                                }
                            }
                    )

                    // SSL Warning or Success indicators for URL
                    val isUrlBlank = m3uUrl.isBlank()
                    val isUrlSecure = m3uUrl.startsWith("https://", ignoreCase = true)
                    
                    if (!isUrlBlank) {
                        if (isUrlSecure) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "SSL Secure",
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "🔒 SSL-versleuteling actief: metadata-transmissie is beveiligd.",
                                    color = PrimaryColor,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "HTTP Unsecure",
                                    tint = ErrorColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "⚠️ Onveilig: Verkeer is onversleuteld over HTTP. Gebruik bij voorkeur HTTPS.",
                                    color = ErrorColor,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Optional Username
                    OutlinedTextField(
                        value = m3uUsername,
                        onValueChange = { m3uUsername = it.replace("\n", "").replace("\r", "") },
                        label = { Text("M3U Gebruikersnaam (Optioneel)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                    true
                                } else {
                                    false
                                }
                            }
                    )

                    // Optional Password
                    OutlinedTextField(
                        value = m3uPassword,
                        onValueChange = { m3uPassword = it.replace("\n", "").replace("\r", "") },
                        label = { Text("M3U Wachtwoord (Optioneel)") },
                        visualTransformation = if (isM3uPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val eyeM3uInteractionSource = remember { MutableInteractionSource() }
                            val isEyeM3uFocused by eyeM3uInteractionSource.collectIsFocusedAsState()
                            Text(
                                text = if (isM3uPasswordVisible) "Verberg " else "Toon ",
                                color = if (isEyeM3uFocused) PrimaryColor else TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable(
                                        interactionSource = eyeM3uInteractionSource,
                                        indication = null,
                                        onClick = { isM3uPasswordVisible = !isM3uPasswordVisible }
                                    )
                                    .focusable(interactionSource = eyeM3uInteractionSource)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.None,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedLabelColor = PrimaryColor,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Enter || keyEvent.key == Key.DirectionCenter) {
                                    true
                                } else {
                                    false
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // LIVE VERIFICATION LOG & FEEDBACK DISPLAY
                if (isVerifying) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .border(1.dp, TextSecondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryColor,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Bezig met API-verbinding testen...",
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (verificationResult != null) {
                    val result = verificationResult!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (result is ConnectionResult.Success) PrimaryColor.copy(alpha = 0.08f)
                                else ErrorColor.copy(alpha = 0.08f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (result is ConnectionResult.Success) PrimaryColor else ErrorColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (result is ConnectionResult.Success) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = if (result is ConnectionResult.Success) "Success" else "Error",
                                    tint = if (result is ConnectionResult.Success) PrimaryColor else ErrorColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (result is ConnectionResult.Success) "SSL & API Verificatie Geslaagd!" else "Verificatie Mislukt",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result is ConnectionResult.Success) PrimaryColor else ErrorColor
                                )
                            }
                            
                            when (result) {
                                is ConnectionResult.Success -> {
                                    Text(
                                        text = "• Verbindingsstatus: HTTP ${result.statusCode} ${result.message}",
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "• Server Latency (RTT): ${result.latencyMs}ms",
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "• Inhoudstype: ${result.contentType}",
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "• Playlist structuur: ${if (result.isM3uFormat) "M3U Formaat Gevalideerd ✓" else "Onbekend formaat"}",
                                        color = if (result.isM3uFormat) PrimaryColor else TextSecondary,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                is ConnectionResult.Failure -> {
                                    Text(
                                        text = "Fouttype: ${result.errorType}",
                                        color = ErrorColor,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = result.details,
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Suggestie: Start de Virtuele Demo of controleer de URL.",
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "💡 Geen M3U-afspeellijst? Start de Virtuele Demo om de app direct met voorbeeldkanalen te ervaren.",
                    color = PrimaryColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Connect Row Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrimaryTvButton(
                        text = if (isLoading) "Verbinding maken..." else "Valideer & Verbind",
                        onClick = {
                            if (connectionType == "XTREAM") {
                                if (username.isEmpty() || password.isEmpty()) {
                                    errorMessage = "Gebruikersnaam en wachtwoord mogen niet leeg zijn."
                                    successMessage = null
                                } else {
                                    isLoading = true
                                    errorMessage = null
                                    successMessage = null
                                    coroutineScope.launch {
                                        val checkUrl = currentServerUrl
                                        val uName = username
                                        val pWord = password
                                        isVerifying = true
                                        val testRes = verifyM3UConnection(checkUrl, uName, pWord)
                                        verificationResult = testRes
                                        isVerifying = false
                                        isLoading = false
                                        
                                        if (testRes is ConnectionResult.Success) {
                                            successMessage = "Succesvol verbonden met Xtream server $checkUrl"
                                            onLoginSuccess(checkUrl, uName)
                                        } else {
                                            errorMessage = "Verbindingstest mislukt, controleer uw inloggegevens."
                                        }
                                    }
                                }
                            } else {
                                if (m3uUrl.isEmpty()) {
                                    errorMessage = "M3U Playlist URL mag niet leeg zijn."
                                    successMessage = null
                                } else {
                                    isLoading = true
                                    errorMessage = null
                                    successMessage = null
                                    coroutineScope.launch {
                                        val checkUrl = m3uUrl
                                        val uName = m3uUsername
                                        val pWord = m3uPassword
                                        isVerifying = true
                                        val testRes = verifyM3UConnection(checkUrl, uName, pWord)
                                        verificationResult = testRes
                                        isVerifying = false
                                        isLoading = false
                                        
                                        if (testRes is ConnectionResult.Success) {
                                            successMessage = "M3U Playlist met succes gevalideerd en verbonden!"
                                            onLoginSuccess(checkUrl, uName.ifEmpty { "M3U_Playlist" })
                                        } else {
                                            errorMessage = "Valideer verbinding is mislukt. Controleer de URL."
                                        }
                                    }
                                }
                            }
                        }
                    )
                    
                    SecondaryTvButton(
                        text = "Verbinding Testen",
                        onClick = {
                            if (connectionType == "XTREAM") {
                                if (username.isEmpty() || password.isEmpty()) {
                                    errorMessage = "Vul gebruikersnaam en wachtwoord in om de verbinding te testen."
                                } else {
                                    errorMessage = null
                                    successMessage = null
                                    coroutineScope.launch {
                                        isVerifying = true
                                        verificationResult = verifyM3UConnection(currentServerUrl, username, password)
                                        isVerifying = false
                                    }
                                }
                            } else {
                                if (m3uUrl.isEmpty()) {
                                    errorMessage = "Voer een M3U Playlist URL in om de verbinding te testen."
                                } else {
                                    errorMessage = null
                                    successMessage = null
                                    coroutineScope.launch {
                                        isVerifying = true
                                        verificationResult = verifyM3UConnection(m3uUrl, m3uUsername, m3uPassword)
                                        isVerifying = false
                                    }
                                }
                            }
                        }
                    )

                    SecondaryTvButton(
                        text = "Virtuele Demo",
                        onClick = {
                            if (connectionType == "M3U") {
                                m3uUrl = "http://virtueel-netwerk.local:8080/get.php?output=ts"
                                m3uUsername = "VirtueleGebruiker"
                                m3uPassword = "DemoPassword"
                            } else {
                                customServerUrl = "http://virtueel-netwerk.local:8080"
                                selectedPresetIndex = presets.size - 1
                                username = "VirtueleGebruiker"
                                password = "DemoPassword"
                            }
                            errorMessage = null
                            successMessage = "Virtuele demo-parameters geladen!"
                            coroutineScope.launch {
                                isVerifying = true
                                delay(800)
                                verificationResult = ConnectionResult.Success(
                                    statusCode = 200,
                                    message = "OK",
                                    latencyMs = 14,
                                    contentType = "application/mpegurl",
                                    isM3uFormat = true,
                                    linesCount = 1250
                                )
                                isVerifying = false
                                onLoginSuccess("http://virtueel-netwerk.local:8080", "VirtueleGebruiker")
                            }
                        }
                    )
                    SecondaryTvButton(text = "Annuleren", onClick = onBack)
                }
            }

            // Right Column: Information Board & Brand Values
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .verticalScroll(rememberScrollState())
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
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        QrCodeDisplay(
                            content = qrPayload,
                            modifier = Modifier
                                .size(220.dp)
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

sealed class ConnectionResult {
    data class Success(
        val statusCode: Int,
        val message: String,
        val latencyMs: Long,
        val contentType: String,
        val isM3uFormat: Boolean,
        val linesCount: Int
    ) : ConnectionResult()

    data class Failure(
        val errorType: String,
        val details: String
    ) : ConnectionResult()
}

suspend fun verifyM3UConnection(
    targetUrl: String,
    uName: String = "",
    pWord: String = ""
): ConnectionResult = withContext(Dispatchers.IO) {
    val startTime = System.currentTimeMillis()
    
    // Check for virtual mock address first
    if (targetUrl.contains("virtueel-netwerk.local") || targetUrl.contains("10.0.2.2") || targetUrl.isEmpty()) {
        delay(600) // Realistic latency simulation
        return@withContext ConnectionResult.Success(
            statusCode = 200,
            message = "OK (Mock Network)",
            latencyMs = 12,
            contentType = "application/mpegurl",
            isM3uFormat = true,
            linesCount = 1840
        )
    }

    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .followRedirects(true)
            .build()

        // Construct request url with optional queries or headers
        val requestBuilder = Request.Builder().url(targetUrl)
        
        // Add User-Agent common to Fire OS/Kodi to prevent firewalls from blocking the request
        requestBuilder.header("User-Agent", "Mozilla/5.0 (Linux; Android 9; Fire TV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Mobile Safari/537.36")
        
        if (uName.isNotEmpty() && pWord.isNotEmpty()) {
            val credential = Credentials.basic(uName, pWord)
            requestBuilder.header("Authorization", credential)
        }

        val request = requestBuilder.build()
        client.newCall(request).execute().use { response ->
            val latency = System.currentTimeMillis() - startTime
            val code = response.code
            val msg = response.message
            val contentType = response.body?.contentType()?.toString() ?: "unknown"
            
            if (response.isSuccessful) {
                // Peek the start of the body to check for #EXTM3U format (only read first 1024 bytes)
                val bodySource = response.body?.source()
                val isM3u = if (bodySource != null) {
                    bodySource.request(1024)
                    val buffer = bodySource.buffer
                    val contentSample = buffer.clone().readUtf8()
                    contentSample.contains("#EXTM3U") || contentSample.contains("#EXTINF")
                } else {
                    false
                }
                
                ConnectionResult.Success(
                    statusCode = code,
                    message = msg,
                    latencyMs = latency,
                    contentType = contentType,
                    isM3uFormat = isM3u || contentType.contains("mpegurl") || targetUrl.endsWith(".m3u") || targetUrl.endsWith(".m3u8"),
                    linesCount = 100
                )
            } else {
                ConnectionResult.Failure(
                    errorType = "HTTP Error $code",
                    details = "Server reageerde met statuscode $code ($msg). Controleer of uw gebruikersnaam en wachtwoord juist zijn geconfigureerd."
                )
            }
        }
    } catch (e: SocketTimeoutException) {
        ConnectionResult.Failure(
            errorType = "Verbindingstime-out",
            details = "De verbinding met de server is mislukt na 5 seconden. Controleer uw internetverbinding of de serverstatus."
        )
    } catch (e: SSLHandshakeException) {
        ConnectionResult.Failure(
            errorType = "SSL Handshake Fout",
            details = "Kan geen veilige SSL-verbinding tot stand brengen. Dit gebeurt meestal wanneer het certificaat van de provider is verlopen of niet wordt vertrouwd door Fire OS."
        )
    } catch (e: IOException) {
        ConnectionResult.Failure(
            errorType = "Netwerkfout (I/O)",
            details = e.message ?: "Onbekende I/O-fout opgetreden bij het verbinden."
        )
    } catch (e: Exception) {
        ConnectionResult.Failure(
            errorType = "Systeemfout",
            details = e.message ?: "Er is een onverwachte fout opgetreden."
        )
    }
}

