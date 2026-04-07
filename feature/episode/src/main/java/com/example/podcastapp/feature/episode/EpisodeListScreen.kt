package com.example.podcastapp.feature.episode

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.player.MiniPlayer
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoShapes
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard
import java.text.SimpleDateFormat
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

@Composable
fun EpisodeListScreen(
    title: String,
    episodes: LazyPagingItems<EpisodeEntity>,
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
    downloadStatusMap: Map<Long, DownloadStatus>,
) {
    Box(modifier = Modifier.fillMaxSize().background(NeoColors.ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(title = title, onBack = onBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(18.dp),
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

        MiniPlayer(
            onTogglePlay = { /* handled internally */ },
            onSeek = { /* handled internally */ },
        )
    }
}

@Composable
private fun EpisodeRow(
    item: EpisodeEntity,
    downloadStatus: DownloadStatus?,
    onEpisodeClick: (Long) -> Unit,
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    ShadowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEpisodeClick(item.id) }
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val imageModifier = Modifier
                    .size(64.dp)
                    .clip(NeoShapes.Card)
                if (item.imageUrl.isNullOrBlank()) {
                    Box(
                        modifier = imageModifier.background(NeoColors.NavBorder)
                    )
                } else {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop,
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    item.pubDate?.let { millis ->
                        Text(
                            text = dateFormatter.format(millis),
                            fontSize = 12.sp,
                            color = NeoColors.TextSecondary,
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
                            fontSize = 12.sp,
                            color = NeoColors.AccentGreen,
                        )
                    }
                }
            }
            item.description?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = NeoColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "Open",
                fontSize = 12.sp,
                color = NeoColors.AccentGreen,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
