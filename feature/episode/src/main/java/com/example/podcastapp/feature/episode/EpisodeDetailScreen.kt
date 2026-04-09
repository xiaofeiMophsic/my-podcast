package com.example.podcastapp.feature.episode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoOutlineButton
import com.example.podcastapp.core.ui.neo.NeoPrimaryButton
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard
import com.example.podcastapp.core.ui.neo.NeoShapes
import com.example.podcastapp.core.ui.utils.htmlToAnnotatedString

@Composable
fun EpisodeDetailRoute(
    onBack: () -> Unit,
    viewModel: EpisodeDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    EpisodeDetailScreen(
        state = state,
        onBack = onBack,
        onDownload = viewModel::download,
    )
}

@Composable
fun EpisodeDetailScreen(
    state: EpisodeDetailUiState,
    onBack: () -> Unit,
    onDownload: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoColors.ScreenBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(title = "Episode", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        state.imageUrl?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .align(Alignment.CenterHorizontally),
                            )
                        }
                        Text(
                            text = state.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        state.pubDate?.let {
                            Text(text = it, fontSize = 12.sp, color = NeoColors.TextSecondary)
                        }
                        state.description?.let {
                            Text(
                                text = it.htmlToAnnotatedString(),
                                fontSize = 13.sp,
                                color = NeoColors.TextSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 260.dp)
                                    .verticalScroll(rememberScrollState()),
                            )
                        }
                    }
                }

                ShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            when (state.downloadStatus) {
                                DownloadStatus.COMPLETED -> {
                                    DownloadProgressButton(
                                        text = "Downloaded",
                                        progress = 1f,
                                        modifier = Modifier.weight(1f),
                                    )
                                }

                                DownloadStatus.DOWNLOADING -> {
                                    DownloadProgressButton(
                                        text = "Downloading…",
                                        progress = state.downloadProgress,
                                        modifier = Modifier.weight(1f),
                                    )
                                }

                                DownloadStatus.FAILED -> {
                                    NeoOutlineButton(
                                        text = "Retry Download",
                                        onClick = onDownload,
                                        modifier = Modifier.weight(1f),
                                    )
                                }

                                else -> {
                                    NeoPrimaryButton(
                                        text = "Download",
                                        onClick = onDownload,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                            NeoOutlineButton(
                                text = "Share",
                                onClick = { /* TODO */ },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadProgressButton(
    text: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val clamped = progress.coerceIn(0f, 1f)
    Surface(
        modifier = modifier.height(44.dp),
        shape = NeoShapes.Card,
        color = NeoColors.CardBg,
        border = BorderStroke(1.dp, NeoColors.CardBorder),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(NeoShapes.Card),
        ) {
            val progressWidth = maxWidth * clamped
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val textMeasurer = rememberTextMeasurer()

                // 平滑动画：处理轮询带来的突兀感
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(
                        durationMillis = if (progress >= 1f) 200 else 800,
                        easing = LinearEasing
                    ),
                    label = "SmoothProgress"
                )

                // 状态切换时的背景色过渡动画
                val animatedBgColor by animateColorAsState(targetValue = NeoColors.CardBg as Color)
                val animatedProgressColor by animateColorAsState(targetValue = NeoColors.AccentGreen)

                val textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .background(animatedBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val progressWidth = width * animatedProgress

                        drawRect(
                            color = animatedProgressColor,
                            size = Size(progressWidth, height)
                        )

                        // 测量文字
                        val textLayoutResult = textMeasurer.measure(
                            text = AnnotatedString(text),
                            style = textStyle
                        )
                        val textOffset = Offset(
                            (width - textLayoutResult.size.width) / 2f,
                            (height - textLayoutResult.size.height) / 2f
                        )

                        drawText(
                            textLayoutResult = textLayoutResult,
                            color = NeoColors.TextPrimary,
                            topLeft = textOffset
                        )

                        if (animatedProgress > 0f) {
                            clipRect(right = progressWidth) {
                                drawText(
                                    textLayoutResult = textLayoutResult,
                                    color = NeoColors.TextOnPrimary,
                                    topLeft = textOffset
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}
