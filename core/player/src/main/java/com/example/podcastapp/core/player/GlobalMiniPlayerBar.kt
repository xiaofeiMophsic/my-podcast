package com.example.podcastapp.core.player

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GlobalMiniPlayerBar(
    onPlayListClick: () -> Unit,
    onPlayerClick: (episodeId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val playerState by viewModel.metadataState.collectAsStateWithLifecycle()
    val progressMsState = viewModel.progressState.collectAsStateWithLifecycle()
    val isPlayingState = viewModel.playingState.collectAsStateWithLifecycle()

    playerState.episodeId?.let { episodeId ->
        Column(modifier = modifier) {
            NeoMiniPlayer(
                title = playerState.title,
                progressMs = { progressMsState.value },
                durationMs = playerState.durationMs,
                isPlaying = { isPlayingState.value },
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onPlayListClick = onPlayListClick,
                onMiniPlayerClick = { onPlayerClick(episodeId)},
                imageUrl = playerState.imageUrl,
            )
        }
    }
}
