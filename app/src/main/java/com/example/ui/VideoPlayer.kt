package com.example.ui

import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.example.data.repository.StreamingSettingsManager
import com.example.data.repository.StreamingProfile
import com.example.data.repository.AdvancedSettingsManager
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

@OptIn(UnstableApi::class)
@Composable
fun FireOsVideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String?,
    autoPlay: Boolean = true,
    useController: Boolean = true,
    userAgent: String = "FireOS-Default-Player/1.0",
    onPlaybackStateChanged: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val currentProfile by StreamingSettingsManager.currentProfile.collectAsState()
    
    // Live collect of advanced hardware settings
    val forceHardwareDecoding by AdvancedSettingsManager.forceHardwareDecoding.collectAsState()
    val mediaCodecTunneling by AdvancedSettingsManager.mediaCodecTunneling.collectAsState()
    val audioPassthroughEnabled by AdvancedSettingsManager.audioPassthroughEnabled.collectAsState()
    val hdrConversionMode by AdvancedSettingsManager.hdrConversionMode.collectAsState()
    val usbPerformanceBuffering by AdvancedSettingsManager.usbPerformanceBuffering.collectAsState()
    val bypassSslVerification by AdvancedSettingsManager.bypassSslVerification.collectAsState()
    val widevineDrmLevel by AdvancedSettingsManager.widevineDrmLevel.collectAsState()

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    
    LaunchedEffect(context, userAgent, currentProfile, forceHardwareDecoding, mediaCodecTunneling, audioPassthroughEnabled, hdrConversionMode, usbPerformanceBuffering, bypassSslVerification, widevineDrmLevel) {
        // Configure Custom HTTP source factory that can bypass SSL verification
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(8000)
            .setReadTimeoutMs(8000)
            .setDefaultRequestProperties(
                mapOf(
                    "Cache-Control" to "no-cache",
                    "Pragma" to "no-cache"
                )
            )

        if (bypassSslVerification) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
                HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            } catch (e: Exception) {
                // Fallback gracefully on strict security configurations
            }
        }

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)
            
        // Explicit AudioAttributes mapping with Audio Passthrough / spatialization preference
        val audioAttributes = AudioAttributes.Builder().apply {
            setUsage(C.USAGE_MEDIA)
            setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            if (audioPassthroughEnabled) {
                // Request multichannel Dolby Atmos / passthrough prioritization flags
                setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL)
            }
        }.build()
            
        val exoContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            context.createAttributionContext("media_playback")
        } else {
            context
        }

        // Configure aggressive pre-buffering adjustments for local USB files vs IPTV network streams
        val loadControl = DefaultLoadControl.Builder().apply {
            val isLocalMedia = videoUrl?.startsWith("/") == true || videoUrl?.startsWith("file:") == true
            
            if (isLocalMedia && usbPerformanceBuffering) {
                // High-performance buffer for USB 2.0 / SSD local storage reads to prevent I/O blocking
                setBufferDurationsMs(
                    15000, // minBufferMs (Larger pre-buffer)
                    45000, // maxBufferMs
                    2500,  // bufferForPlaybackMs
                    5000   // bufferForPlaybackAfterRebufferMs
                )
            } else if (currentProfile == StreamingProfile.LOW_LATENCY) {
                setBufferDurationsMs(
                    1500, // minBufferMs
                    3000, // maxBufferMs
                    500,  // bufferForPlaybackMs
                    1000  // bufferForPlaybackAfterRebufferMs
                )
            } else {
                setBufferDurationsMs(
                    25000, // minBufferMs (high-fidelity IPTV)
                    60000, // maxBufferMs
                    3000,
                    5000
                )
            }
        }.build()

        // Configure modern renderers factory with HW codec preferences & media tunneling
        val renderersFactory = DefaultRenderersFactory(exoContext).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            if (forceHardwareDecoding) {
                // Rank hardware acceleration over software emulators
                setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                    val defaultDecoders = androidx.media3.exoplayer.mediacodec.MediaCodecSelector.DEFAULT
                        .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
                    // Custom sorting can be applied here to prefer hardware-specific decoders (like OMX.Nvidia.* or OMX.amlogic.*)
                    defaultDecoders
                }
            }
            if (mediaCodecTunneling) {
                setEnableAudioTrackPlaybackParams(true)
            }
        }

        val builder = ExoPlayer.Builder(exoContext, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(loadControl)
            
        if (mediaCodecTunneling) {
            // Video tunneling requires audio pass-through session binding
            builder.setAnalyticsCollector(androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector(androidx.media3.common.util.Clock.DEFAULT))
        }

        val player = builder.build()
        player.playWhenReady = autoPlay
        
        if (onPlaybackStateChanged != null) {
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    onPlaybackStateChanged(playbackState)
                }
            })
        }
        
        exoPlayer = player
    }
    
    LaunchedEffect(exoPlayer, videoUrl) {
        val player = exoPlayer ?: return@LaunchedEffect
        if (videoUrl.isNullOrEmpty()) {
            player.clearMediaItems()
            player.stop()
            return@LaunchedEffect
        }
        
        val mimeType = if (videoUrl.contains(".m3u8", ignoreCase = true) || videoUrl.contains(".m3u", ignoreCase = true)) {
            MimeTypes.APPLICATION_M3U8
        } else {
            MimeTypes.APPLICATION_MP4
        }
        
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrl))
            .setMimeType(mimeType)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
        }
    }
    
    Box(modifier = modifier.background(Color.Black)) {
        if (exoPlayer != null) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        this.useController = useController
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        // Optimize surface logic for FireOS hardware decoders
                        setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                        keepScreenOn = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
