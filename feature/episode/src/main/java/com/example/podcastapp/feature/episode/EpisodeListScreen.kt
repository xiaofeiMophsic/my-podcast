package com.example.podcastapp.feature.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.player.MiniPlayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EpisodeListRoute(
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: EpisodeListViewModel = hiltViewModel(),
) {
    val title by viewModel.title.collectAsState()
    val episodes = viewModel.paging.collectAsLazyPagingItems()
    val downloadStatusMap by viewModel.downloadStatusMap.collectAsState()

    EpisodeListScreen(
        title = title,
        episodes = episodes,
        onEpisodeClick = onEpisodeClick,
        onBack = onBack,
        downloadStatusMap = downloadStatusMap,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeListScreen(
    title: String,
    episodes: LazyPagingItems<EpisodeEntity>,
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
    downloadStatusMap: Map<Long, DownloadStatus>,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        bottomBar = {
            // MiniPlayer 会内部使用自己的 PlayerViewModel，不需要我们传递回调
            MiniPlayer(
                onTogglePlay = { /* 内部处理 */ },
                onSeek = { /* 内部处理 */ }
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = episodes.itemCount,
                key = episodes.itemKey { it.id },
            ) { index ->
                val item = episodes[index]
                if (item != null) {
                    val status = downloadStatusMap[item.id]
                    EpisodeRow(item, status, onEpisodeClick)
                }
            }
        }
    }
}

@Composable
private fun EpisodeRow(
    item: EpisodeEntity,
    downloadStatus: DownloadStatus?,
    onEpisodeClick: (Long) -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth(), onClick = { onEpisodeClick(item.id) }) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            item.pubDate?.let { millis ->
                Text(
                    text = dateFormatter.format(Date(millis)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            downloadStatus?.let { status ->
                Text(
                    text = when (status) {
                        DownloadStatus.QUEUED -> "Download queued"
                        DownloadStatus.DOWNLOADING -> "Downloading"
                        DownloadStatus.COMPLETED -> "Downloaded"
                        DownloadStatus.FAILED -> "Download failed"
                        DownloadStatus.CANCELED -> "Download canceled"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            item.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
