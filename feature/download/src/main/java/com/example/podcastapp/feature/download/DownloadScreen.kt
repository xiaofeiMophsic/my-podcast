package com.example.podcastapp.feature.download

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard

@Composable
fun DownloadRoute(
    onBack: () -> Unit,
    viewModel: DownloadViewModel = hiltViewModel(),
) {
    val downloads by viewModel.uiState.collectAsState()
    DownloadScreen(uiState = downloads, onBack = onBack)
}

@Composable
fun DownloadScreen(
    uiState: DownloadScreenUiState,
    onBack: () -> Unit,
) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(NeoColors.ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(title = stringResource(R.string.download_management_title), onBack = onBack)

            AnimatedVisibility(visible = uiState.isEmpty) {
                Text(
                    text = stringResource(R.string.no_downloads_yet),
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
                if (uiState.activeItems.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.in_progress),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.TextPrimary,
                        )
                    }
                    items(
                        items = uiState.activeItems,
                        key = { it.id }
                    ) { item ->
                        DownloadCard(item = item, showProgress = true)
                    }
                }

                if (uiState.historyItems.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.history),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.TextPrimary,
                            modifier = Modifier.padding(top = if (uiState.activeItems.isNotEmpty()) 6.dp else 0.dp),
                        )
                    }
                    items(
                        items = uiState.historyItems,
                        key = { it.id }
                    ) { item ->
                        DownloadCard(item = item, showProgress = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadCard(
    item: DownloadItemUiState,
    showProgress: Boolean,
) {

    val progress by remember {
        derivedStateOf { item.progress }
    }

    // 2. 将瞬移的进度变成平滑动画
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, // 或者自定义 spring()
        label = "progressAnimation"
    )

    ShadowCard(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize()) {
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

            if (showProgress) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = NeoColors.AccentGreen,
                    trackColor = NeoColors.NavBorder,
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = NeoColors.TextSecondary,
                )
            }

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

private fun statusLabel(status: DownloadStatus): String {
    return when (status) {
        DownloadStatus.QUEUED -> "Queued"
        DownloadStatus.DOWNLOADING -> "Downloading"
        DownloadStatus.COMPLETED -> "Completed"
        DownloadStatus.FAILED -> "Failed"
        DownloadStatus.CANCELED -> "Canceled"
    }
}
