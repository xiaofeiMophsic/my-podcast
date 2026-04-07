package com.example.podcastapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.podcastapp.core.player.NowPlayingRoute
import com.example.podcastapp.feature.download.DownloadRoute
import com.example.podcastapp.feature.episode.EpisodeDetailRoute
import com.example.podcastapp.feature.episode.EpisodeListRoute
import com.example.podcastapp.feature.episode.SearchRoute
import com.example.podcastapp.feature.podcast.AddRssRoute
import com.example.podcastapp.feature.podcast.HomeRoute
import com.example.podcastapp.feature.podcast.PodcastListRoute
import com.example.podcastapp.navigation.NavRoutes
import com.example.podcastapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { /* 权限结果处理 */ }
                    )
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.HOME) {
        composable(NavRoutes.HOME) {
            HomeRoute(
                onSearchClick = { navController.navigate(NavRoutes.SEARCH) },
                onPlayerClick = { navController.navigate(NavRoutes.NOW_PLAYING) },
                onAddRssClick = { navController.navigate(NavRoutes.ADD_RSS) },
            )
        }
        composable(NavRoutes.NOW_PLAYING) {
            NowPlayingRoute(onBack = { navController.popBackStack() })
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
                    navController.navigate(NavRoutes.episodeDetailRoute(episodeId))
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
