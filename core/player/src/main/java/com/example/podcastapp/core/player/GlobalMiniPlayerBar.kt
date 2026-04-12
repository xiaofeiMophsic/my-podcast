package com.example.podcastapp.core.player

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun GlobalMiniPlayerBar(
    onPlayListClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val playerState by viewModel.playerState.collectAsState()
    val progressMsState = viewModel.progressState.collectAsState()

    if (playerState.episodeId != null) {
        Column(modifier = modifier) {
            NeoMiniPlayer(
                title = playerState.title,
                progressMs = { progressMsState.value },
                durationMs = playerState.durationMs,
                isPlaying = playerState.isPlaying,
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onPlayListClick = onPlayListClick,
                imageUrl = playerState.imageUrl,
            )
        }
    }
}
