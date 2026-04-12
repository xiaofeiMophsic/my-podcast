package com.example.podcastapp

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
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


