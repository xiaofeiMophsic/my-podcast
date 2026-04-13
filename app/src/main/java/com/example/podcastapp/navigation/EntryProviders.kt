package com.example.podcastapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.podcastapp.core.navigation.AddRssNavKey
import com.example.podcastapp.core.navigation.DownloadsNavKey
import com.example.podcastapp.core.navigation.EpisodeDetailNavKey
import com.example.podcastapp.core.navigation.EpisodeListNavKey
import com.example.podcastapp.core.navigation.ExploreNavKey
import com.example.podcastapp.core.navigation.FavouritesNavKey
import com.example.podcastapp.core.navigation.HomeNavKey
import com.example.podcastapp.core.navigation.Navigator
import com.example.podcastapp.core.navigation.NowPlayingNavKey
import com.example.podcastapp.core.navigation.SearchNavKey
import com.example.podcastapp.core.navigation.navigateToAddRss
import com.example.podcastapp.core.navigation.navigateToDownloads
import com.example.podcastapp.core.navigation.navigateToEpisodeDetail
import com.example.podcastapp.core.navigation.navigateToEpisodeList
import com.example.podcastapp.core.navigation.navigateToNowPlaying
import com.example.podcastapp.core.player.NowPlayingRoute
import com.example.podcastapp.feature.download.DownloadRoute
import com.example.podcastapp.feature.episode.EpisodeDetailRoute
import com.example.podcastapp.feature.episode.EpisodeListRoute
import com.example.podcastapp.feature.episode.SearchRoute
import com.example.podcastapp.feature.podcast.AddRssRoute
import com.example.podcastapp.feature.podcast.HomeRoute
import com.example.podcastapp.feature.podcast.PodcastListRoute

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeRoute(
            onPlayerClick = { navigator.navigateToNowPlaying(it) },
            onAddRssClick = { navigator.navigateToAddRss() },
        )
    }
}

fun EntryProviderScope<NavKey>.exploreEntry(navigator: Navigator) {
    entry<ExploreNavKey> {
        PodcastListRoute(
            onPodcastClick = { navigator.navigateToEpisodeList(it) },
            onSearchClick = { navigator.navigate(SearchNavKey) },
            onDownloadsClick = { navigator.navigateToDownloads() },
        )
    }
}

fun EntryProviderScope<NavKey>.favouritesEntry(navigator: Navigator) {
    entry<FavouritesNavKey> {
        // TODO: Implement Favourites screen
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Favourites - Coming Soon")
        }
    }
}

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey> {
        SearchRoute(
            onEpisodeClick = { navigator.navigateToNowPlaying(it) },
            onBack = { navigator.goBack() },
        )
    }
}

fun EntryProviderScope<NavKey>.nowPlayingEntry(navigator: Navigator) {
    entry<NowPlayingNavKey> { key ->
        val targetId = key.targetEpisodeId.takeIf { it > 0L }
        NowPlayingRoute(
            onBack = { navigator.goBack() },
            targetEpisodeId = targetId,
            onOpenDownloads = { navigator.navigateToDownloads() },
        )
    }
}

fun EntryProviderScope<NavKey>.episodeListEntry(navigator: Navigator) {
    entry<EpisodeListNavKey> {
        EpisodeListRoute(
            onEpisodeClick = { navigator.navigateToEpisodeDetail(it) },
            onBack = { navigator.goBack() },
        )
    }
}

fun EntryProviderScope<NavKey>.episodeDetailEntry(navigator: Navigator) {
    entry<EpisodeDetailNavKey> {
        EpisodeDetailRoute(
            onBack = { navigator.goBack() },
        )
    }
}

fun EntryProviderScope<NavKey>.downloadEntry(navigator: Navigator) {
    entry<DownloadsNavKey> {
        DownloadRoute(
            onBack = { navigator.goBack() },
        )
    }
}

fun EntryProviderScope<NavKey>.addRssEntry(navigator: Navigator) {
    entry<AddRssNavKey> {
        AddRssRoute(
            onBack = { navigator.goBack() },
        )
    }
}
