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
import com.example.data.repository.StreamingSettingsManager
import com.example.data.repository.StreamingProfile

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
    
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    
    LaunchedEffect(context, userAgent, currentProfile) {
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

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)
            
        // Explicit AudioAttributes mapping for proper FireOS volume/ducking handling
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()
            
        val exoContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            object : android.content.ContextWrapper(context.createAttributionContext("media_playback")) {
                override fun getApplicationContext(): android.content.Context = this
            }
        } else {
            context
        }

        val loadControl = DefaultLoadControl.Builder().apply {
            if (currentProfile == StreamingProfile.LOW_LATENCY) {
                setBufferDurationsMs(
                    1500, // minBufferMs
                    3000, // maxBufferMs
                    500,  // bufferForPlaybackMs
                    1000  // bufferForPlaybackAfterRebufferMs
                )
            }
        }.build()

        val player = ExoPlayer.Builder(exoContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(loadControl)
            .build()
            
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
