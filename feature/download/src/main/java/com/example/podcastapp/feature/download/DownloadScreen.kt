package com.example.podcastapp.feature.download

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard

@Composable
fun DownloadRoute(
    onBack: () -> Unit,
    viewModel: DownloadViewModel = hiltViewModel(),
) {
    val downloads by viewModel.downloads.collectAsState()
    DownloadScreen(downloads = downloads, onBack = onBack)
}

@Composable
fun DownloadScreen(
    downloads: List<DownloadEntity>,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(NeoColors.ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(title = "Downloads", onBack = onBack)

            AnimatedVisibility(visible = downloads.isEmpty()) {
                Text(
                    text = "No downloads yet",
                    fontSize = 12.sp,
                    color = NeoColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(downloads) { item ->
                    ShadowCard(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "Episode #${item.episodeId}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoColors.TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = statusLabel(item.status),
                                fontSize = 12.sp,
                                color = NeoColors.AccentGreen,
                            )
                            item.localPath?.let {
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    color = NeoColors.TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun statusLabel(status: DownloadStatus): String {
    return when (status) {
        DownloadStatus.QUEUED -> "Queued"
        DownloadStatus.DOWNLOADING -> "Downloading"
        DownloadStatus.COMPLETED -> "Completed"
        DownloadStatus.FAILED -> "Failed"
        DownloadStatus.CANCELED -> "Canceled"
    }
}
