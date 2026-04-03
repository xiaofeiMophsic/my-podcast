package com.example.podcastapp.core.media

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class PlayerController(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    internal val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    init {
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateState(isPlaying = isPlaying)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateState(durationMs = player.duration.coerceAtLeast(0L))
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    updateState(
                        title = mediaMetadata.title?.toString(),
                        artist = mediaMetadata.artist?.toString(),
                        imageUrl = mediaMetadata.artworkUri?.toString()
                    )
                }
            }
        )

        scope.launch {
            while (isActive) {
                updateState(positionMs = player.currentPosition.coerceAtLeast(0L))
                delay(500)
            }
        }
    }

    fun playEpisode(episodeId: Long, title: String, url: String, artist: String? = null, imageUrl: String? = null) {
        ensureServiceStarted()
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setArtworkUri(imageUrl?.toUri())
            .build()
            
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(episodeId.toString())
            .setMediaMetadata(mediaMetadata)
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        updateState(title = title, artist = artist, imageUrl = imageUrl, episodeId = episodeId)
    }

    fun play() {
        ensureServiceStarted()
        player.play()
    }

    fun pause() = player.pause()

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    private fun ensureServiceStarted() {
        val intent = Intent(context, PlaybackService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    private fun updateState(
        isPlaying: Boolean? = null,
        title: String? = null,
        artist: String? = null,
        imageUrl: String? = null,
        positionMs: Long? = null,
        durationMs: Long? = null,
        episodeId: Long? = null,
    ) {
        val current = _state.value
        _state.value = current.copy(
            isPlaying = isPlaying ?: current.isPlaying,
            title = title ?: current.title,
            artist = artist ?: current.artist,
            imageUrl = imageUrl ?: current.imageUrl,
            positionMs = positionMs ?: current.positionMs,
            durationMs = durationMs ?: current.durationMs,
            episodeId = episodeId ?: current.episodeId,
        )
    }
}
