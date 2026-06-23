package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Screen {
    SPLASH, HOME, DETAILS, PLAYER, OFFLINE_LIBRARY, PRIVACY, EXTERNAL_PLAYER, SETTINGS, XTREAM_LOGIN, XTREAM_MANUAL, RECOMMENDED_APPS, AMBIENT, DIAGNOSTICS
}

@Composable
fun PremiumTvApp() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(PrimaryDark.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(size.width * 0.2f, size.height * 0.1f),
                            radius = size.width * 0.8f
                        )
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentColor.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width * 0.9f, size.height * 0.8f),
                            radius = size.width * 0.6f
                        )
                    )
                }
        ) {
            val navController = rememberNavController()
            var isXtreamConnected by remember { mutableStateOf(false) }
            var connectedServerUrl by remember { mutableStateOf("") }
            var connectedUser by remember { mutableStateOf("") }
            
            NavHost(navController = navController, startDestination = Screen.SPLASH.name) {
                composable(Screen.SPLASH.name) {
                    SplashScreen(
                        onSplashComplete = { 
                            navController.navigate(Screen.HOME.name) {
                                popUpTo(Screen.SPLASH.name) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.HOME.name) {
                    HomeScreen(
                        isXtreamConnected = isXtreamConnected,
                        connectedServerUrl = connectedServerUrl,
                        connectedUser = connectedUser,
                        onNavigateToDetails = { navController.navigate(Screen.DETAILS.name) },
                        onNavigateToExternal = { navController.navigate(Screen.EXTERNAL_PLAYER.name) },
                        onNavigateToOffline = { navController.navigate(Screen.OFFLINE_LIBRARY.name) },
                        onNavigateToPrivacy = { navController.navigate(Screen.PRIVACY.name) },
                        onNavigateToSettings = { navController.navigate(Screen.SETTINGS.name) },
                        onNavigateToXtream = { navController.navigate(Screen.XTREAM_LOGIN.name) },
                        onNavigateToManual = { navController.navigate(Screen.XTREAM_MANUAL.name) },
                        onNavigateToApps = { navController.navigate(Screen.RECOMMENDED_APPS.name) },
                        onNavigateToAmbient = { navController.navigate(Screen.AMBIENT.name) }
                    )
                }
                composable(Screen.DETAILS.name) {
                    DetailsScreen(
                        onBack = { navController.popBackStack() },
                        onPlay = { navController.navigate(Screen.PLAYER.name) }
                    )
                }
                composable(Screen.PLAYER.name) {
                    PlayerScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.OFFLINE_LIBRARY.name) {
                    OfflineLibraryScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.PRIVACY.name) {
                    PrivacySettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.EXTERNAL_PLAYER.name) {
                    ExternalPlayerScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.SETTINGS.name) {
                    SettingsScreen(
                        isXtreamConnected = isXtreamConnected,
                        connectedServerUrl = connectedServerUrl,
                        connectedUser = connectedUser,
                        onDisconnect = {
                            isXtreamConnected = false
                            connectedServerUrl = ""
                            connectedUser = ""
                        },
                        onNavigateToDiagnostics = { navController.navigate(Screen.DIAGNOSTICS.name) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.XTREAM_LOGIN.name) {
                    XtreamLoginScreen(
                        onBack = { navController.popBackStack() },
                        onLoginSuccess = { serverUrl, uName ->
                            isXtreamConnected = true
                            connectedServerUrl = serverUrl
                            connectedUser = uName
                            navController.navigate(Screen.HOME.name) {
                                popUpTo(Screen.HOME.name) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.XTREAM_MANUAL.name) {
                    XtreamInstructionScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.RECOMMENDED_APPS.name) {
                    SupportedAppsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AMBIENT.name) {
                    AmbientScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.DIAGNOSTICS.name) {
                    DiagnosticScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
