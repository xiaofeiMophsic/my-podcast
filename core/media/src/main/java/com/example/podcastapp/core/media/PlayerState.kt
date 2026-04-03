package com.example.podcastapp.core.media

data class PlayerState(
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String? = null,
    val imageUrl: String? = null,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val episodeId: Long? = null,
)
