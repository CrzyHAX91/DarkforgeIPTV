package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.AccentColor
import com.example.ui.theme.AppTheme
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryDark

enum class Screen {
    SPLASH,
    HOME,
    DETAILS,
    PLAYER,
    OFFLINE_LIBRARY,
    PRIVACY,
    EXTERNAL_PLAYER,
    SETTINGS,
    XTREAM_LOGIN,
    XTREAM_MANUAL,
    RECOMMENDED_APPS,
    AMBIENT,
    DIAGNOSTICS
}

private data class XtreamConnectionState(
    val isConnected: Boolean = false,
    val serverUrl: String = "",
    val userName: String = ""
)

@Composable
fun PremiumTvApp() {
    AppTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        var isXtreamConnected by rememberSaveable { mutableStateOf(false) }
        var connectedServerUrl by rememberSaveable { mutableStateOf("") }
        var connectedUser by rememberSaveable { mutableStateOf("") }

        val xtreamState = XtreamConnectionState(
            isConnected = isXtreamConnected,
            serverUrl = connectedServerUrl,
            userName = connectedUser
        )

        val isFullScreenRoute = currentRoute in setOf(
            Screen.SPLASH.name,
            Screen.PLAYER.name
        )

        val appContentModifier = if (isFullScreenRoute) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .premiumBackground()
        ) {
            Box(modifier = appContentModifier) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.SPLASH.name
                ) {
                    composable(Screen.SPLASH.name) {
                        SplashScreen(
                            onSplashComplete = {
                                navController.navigate(Screen.HOME.name) {
                                    popUpTo(Screen.SPLASH.name) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(Screen.HOME.name) {
                        HomeScreen(
                            isXtreamConnected = xtreamState.isConnected,
                            connectedServerUrl = xtreamState.serverUrl,
                            connectedUser = xtreamState.userName,
                            onNavigateToDetails = {
                                navController.navigateSingleTop(Screen.DETAILS)
                            },
                            onNavigateToExternal = {
                                navController.navigateSingleTop(Screen.EXTERNAL_PLAYER)
                            },
                            onNavigateToOffline = {
                                navController.navigateSingleTop(Screen.OFFLINE_LIBRARY)
                            },
                            onNavigateToPrivacy = {
                                navController.navigateSingleTop(Screen.PRIVACY)
                            },
                            onNavigateToSettings = {
                                navController.navigateSingleTop(Screen.SETTINGS)
                            },
                            onNavigateToXtream = {
                                navController.navigateSingleTop(Screen.XTREAM_LOGIN)
                            },
                            onNavigateToManual = {
                                navController.navigateSingleTop(Screen.XTREAM_MANUAL)
                            },
                            onNavigateToApps = {
                                navController.navigateSingleTop(Screen.RECOMMENDED_APPS)
                            },
                            onNavigateToAmbient = {
                                navController.navigateSingleTop(Screen.AMBIENT)
                            }
                        )
                    }

                    composable(Screen.DETAILS.name) {
                        DetailsScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onPlay = {
                                navController.navigateSingleTop(Screen.PLAYER)
                            }
                        )
                    }

                    composable(Screen.PLAYER.name) {
                        PlayerScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.OFFLINE_LIBRARY.name) {
                        OfflineLibraryScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.PRIVACY.name) {
                        PrivacySettingsScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.EXTERNAL_PLAYER.name) {
                        ExternalPlayerScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.SETTINGS.name) {
                        SettingsScreen(
                            isXtreamConnected = xtreamState.isConnected,
                            connectedServerUrl = xtreamState.serverUrl,
                            connectedUser = xtreamState.userName,
                            onDisconnect = {
                                isXtreamConnected = false
                                connectedServerUrl = ""
                                connectedUser = ""
                            },
                            onNavigateToDiagnostics = {
                                navController.navigateSingleTop(Screen.DIAGNOSTICS)
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.XTREAM_LOGIN.name) {
                        XtreamLoginScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onLoginSuccess = { serverUrl, userName ->
                                isXtreamConnected = true
                                connectedServerUrl = serverUrl
                                connectedUser = userName

                                navController.navigate(Screen.HOME.name) {
                                    popUpTo(Screen.HOME.name) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(Screen.XTREAM_MANUAL.name) {
                        XtreamInstructionScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.RECOMMENDED_APPS.name) {
                        SupportedAppsScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.AMBIENT.name) {
                        AmbientScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.DIAGNOSTICS.name) {
                        DiagnosticScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.premiumBackground(): Modifier {
    return drawBehind {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryDark.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(
                    x = size.width * 0.2f,
                    y = size.height * 0.1f
                ),
                radius = size.width * 0.8f
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentColor.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(
                    x = size.width * 0.9f,
                    y = size.height * 0.8f
                ),
                radius = size.width * 0.6f
            )
        )
    }
}

private fun NavController.navigateSingleTop(screen: Screen) {
    navigate(screen.name) {
        launchSingleTop = true
        restoreState = true
    }
}
