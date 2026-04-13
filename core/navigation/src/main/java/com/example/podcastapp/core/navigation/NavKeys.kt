package com.example.podcastapp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── Top-level Tabs ──

@Serializable
object HomeNavKey : NavKey

@Serializable
object ExploreNavKey : NavKey

@Serializable
object FavouritesNavKey : NavKey

@Serializable
object SearchNavKey : NavKey

// ── Detail / Sub pages ──

@Serializable
data class EpisodeListNavKey(val podcastId: Long) : NavKey

@Serializable
data class EpisodeDetailNavKey(val episodeId: Long) : NavKey

@Serializable
data class NowPlayingNavKey(val targetEpisodeId: Long = -1L) : NavKey

@Serializable
object DownloadsNavKey : NavKey

@Serializable
object AddRssNavKey : NavKey

// ── Navigator extension functions ──

fun Navigator.navigateToEpisodeList(podcastId: Long) {
    navigate(EpisodeListNavKey(podcastId))
}

fun Navigator.navigateToEpisodeDetail(episodeId: Long) {
    navigate(EpisodeDetailNavKey(episodeId))
}

fun Navigator.navigateToNowPlaying(episodeId: Long? = null) {
    navigate(NowPlayingNavKey(episodeId ?: -1L))
}

fun Navigator.navigateToDownloads() {
    navigate(DownloadsNavKey)
}

fun Navigator.navigateToAddRss() {
    navigate(AddRssNavKey)
}
