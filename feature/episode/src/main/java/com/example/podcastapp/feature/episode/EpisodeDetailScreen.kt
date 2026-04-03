package com.example.podcastapp.feature.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.podcastapp.core.database.DownloadStatus

@Composable
fun EpisodeDetailRoute(
    onBack: () -> Unit,
    viewModel: EpisodeDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    EpisodeDetailScreen(
        state = state,
        onBack = onBack,
        onPlay = viewModel::play,
        onDownload = viewModel::download,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(
    state: EpisodeDetailUiState,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Episode") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(state.title, style = MaterialTheme.typography.titleLarge)
            state.pubDate?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
            state.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onPlay) {
                    Text("Play")
                }
                when (state.downloadStatus) {
                    DownloadStatus.COMPLETED -> {
                        OutlinedButton(onClick = onPlay) {
                            Text("Play Offline")
                        }
                    }
                    DownloadStatus.DOWNLOADING -> {
                        OutlinedButton(onClick = {}, enabled = false) {
                            Text("Downloading…")
                        }
                    }
                    DownloadStatus.FAILED -> {
                        OutlinedButton(onClick = onDownload) {
                            Text("Retry Download")
                        }
                    }
                    else -> {
                        Button(onClick = onDownload) {
                            Text("Download")
                        }
                    }
                }
            }
        }
    }
}
