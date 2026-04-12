package com.example.podcastapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.podcastapp.core.player.NowPlayingRoute
import com.example.podcastapp.core.ui.R
import com.example.podcastapp.core.ui.neo.NeoNavItem
import com.example.podcastapp.feature.download.DownloadRoute
import com.example.podcastapp.feature.episode.EpisodeDetailRoute
import com.example.podcastapp.feature.episode.EpisodeListRoute
import com.example.podcastapp.feature.episode.SearchRoute
import com.example.podcastapp.feature.podcast.AddRssRoute
import com.example.podcastapp.feature.podcast.HomeRoute
import com.example.podcastapp.feature.podcast.PodcastListRoute
import com.example.podcastapp.navigation.NavRoutes

@Composable
private fun AppRoot() {

    val NavItems = listOf(
        NeoNavItem(
            stringResource(R.string.nav_home), R.drawable.nav_home, "home"),
        NeoNavItem(
            stringResource(R.string.nav_explore), R.drawable.nav_explore, "explore"
        ), NeoNavItem(
            stringResource(R.string.nav_favourites), R.drawable.nav_like, "favourites"
        ), NeoNavItem(
            stringResource(R.string.nav_search), R.drawable.nav_setting, "search"
        )
    )

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.HOME) {
        composable(NavRoutes.HOME) {
            HomeRoute(
                onSearchClick = { navController.navigate(NavRoutes.SEARCH) },
                onPlayerClick = { episodeId -> navController.navigate(NavRoutes.nowPlayingRoute(episodeId)) },
                onAddRssClick = { navController.navigate(NavRoutes.ADD_RSS) },
            )
        }
        composable(
            route = "${NavRoutes.NOW_PLAYING}?${NavRoutes.ARG_TARGET_EPISODE_ID}={${NavRoutes.ARG_TARGET_EPISODE_ID}}",
            arguments = listOf(
                navArgument(NavRoutes.ARG_TARGET_EPISODE_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            ),
        ) { backStackEntry ->
            val targetEpisodeId = backStackEntry.arguments
                ?.getLong(NavRoutes.ARG_TARGET_EPISODE_ID)
                ?.takeIf { it > 0L }
            NowPlayingRoute(
                onBack = { navController.popBackStack() },
                targetEpisodeId = targetEpisodeId,
                onOpenDownloads = { navController.navigate(NavRoutes.DOWNLOADS) },
            )
        }
        composable(NavRoutes.PODCAST_LIST) {
            PodcastListRoute(
                onPodcastClick = { podcastId ->
                    navController.navigate(NavRoutes.episodeListRoute(podcastId))
                },
                onSearchClick = { navController.navigate(NavRoutes.SEARCH) },
                onDownloadsClick = { navController.navigate(NavRoutes.DOWNLOADS) },
            )
        }
        composable(NavRoutes.SEARCH) {
            SearchRoute(
                onEpisodeClick = { episodeId ->
                    navController.navigate(NavRoutes.nowPlayingRoute(episodeId))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavRoutes.DOWNLOADS) {
            DownloadRoute(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.ADD_RSS) {
            AddRssRoute(onBack = { navController.popBackStack() })
        }
        composable(
            route = "${NavRoutes.EPISODE_LIST}/{${NavRoutes.ARG_PODCAST_ID}}",
            arguments = listOf(navArgument(NavRoutes.ARG_PODCAST_ID) { type = NavType.LongType }),
        ) {
            EpisodeListRoute(
                onEpisodeClick = { episodeId ->
                    navController.navigate(NavRoutes.episodeDetailRoute(episodeId))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "${NavRoutes.EPISODE_DETAIL}/{${NavRoutes.ARG_EPISODE_ID}}",
            arguments = listOf(navArgument(NavRoutes.ARG_EPISODE_ID) { type = NavType.LongType }),
        ) {
            EpisodeDetailRoute(onBack = { navController.popBackStack() })
        }
    }
}