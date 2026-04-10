package com.example.podcastapp.core.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.podcastapp.feature.player.R
import com.example.podcastapp.core.ui.neo.ShadowCard
import kotlin.math.roundToInt
import kotlin.random.Random

// --- Design tokens ---
private val ScreenBg = Color(0xFFFFFFED)  // #FFFFED
private val AccentGreen = Color(0xFF87B800)
private val WaveGray = Color(0xFFC4C4C4)
private val TextPrimary = Color(0xFF000000)
private val CardBorder = Color(0xFF000000)
private val AlbumCardBg = Color(0xFFFFFFFF)

@JvmInline
value class WaveBar(val height: Float)

// Asset URLs (Figma MCP, valid 7 days)
private const val ALBUM_COVER =
    "https://www.figma.com/api/mcp/asset/6ee017b6-7ee4-4703-bf43-f4d9ad27bafd"
private const val DOTS_DECO =
    "https://www.figma.com/api/mcp/asset/23cb03d4-760f-433f-abf3-022b992aa0e8"
private const val IC_BACK =
    "https://www.figma.com/api/mcp/asset/000d493b-3ea5-47a6-b2d1-9f9a35b6109c"
private const val IC_HEART =
    "https://www.figma.com/api/mcp/asset/0f849085-4cba-46aa-945c-b1e38fc2bf11"
private const val IC_REPEAT =
    "https://www.figma.com/api/mcp/asset/2c558165-f0fb-47b5-94b0-46b833787304"
private const val IC_PREV =
    "https://www.figma.com/api/mcp/asset/430f9f9f-5824-42b3-8bc9-7d41811abed7"
private const val IC_PLAY =
    "https://www.figma.com/api/mcp/asset/a41687f2-4e16-41fb-8641-d5a55b88bb8a"
private const val IC_NEXT =
    "https://www.figma.com/api/mcp/asset/58602731-3143-4af5-83c6-7cd52b71b5b7"
private const val IC_MORE =
    "https://www.figma.com/api/mcp/asset/94acd51b-3167-4226-95d6-1f7fccf44851"


@Composable
fun NowPlayingRoute(
    onBack: () -> Unit,
    targetEpisodeId: Long? = null,
    onOpenDownloads: () -> Unit = {},
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.metadataState.collectAsState()
    val detail by viewModel.episodeDetail.collectAsState()
    val isGenerating by viewModel.isGeneratingWaveform.collectAsState()
    val waveformBars by viewModel.waveformBars.collectAsState()

    LaunchedEffect(targetEpisodeId) {
        targetEpisodeId?.let { viewModel.playEpisodeById(it) }
    }
    LaunchedEffect(state.episodeId) {
//        viewModel.refreshEpisodeDetail(state.episodeId)
    }

    NowPlayingScreen(
        title = state.title.ifBlank { "Take My Breath" },
        artist = state.artist ?: "The Weeknd",
        imageUrl = state.imageUrl ?: ALBUM_COVER,
        isGeneratingWaveform = isGenerating,
        isPlaying = { viewModel.playingState.collectAsState().value },
        durationMs = state.durationMs,
        progress = {
            val currentProgress = viewModel.progressState.collectAsState().value
            if (state.durationMs > 0) {
                (currentProgress.toFloat() / state.durationMs).coerceIn(0f, 1f)
            } else 0f
        },
        waveformBars = waveformBars,
        detail = detail,
        onBack = onBack,
        onOpenDownloads = onOpenDownloads,
        onTogglePlay = { viewModel.togglePlayPause() },
        onSeekPrev = { viewModel.seekTo(maxOf(0, viewModel.progressState.value - 10_000)) },
        onSeekNext = {
            viewModel.seekTo(
                minOf(
                    state.durationMs,
                    viewModel.progressState.value + 30_000
                )
            )
        },
        onSeekTo = { fraction ->
            val target = (state.durationMs * fraction).toLong().coerceAtLeast(0L)
            viewModel.seekTo(target)
        },
    )
}

@Composable
fun NowPlayingScreen(
    title: String,
    artist: String,
    imageUrl: String,
    isGeneratingWaveform: Boolean,
    isPlaying: @Composable ()->Boolean,
    durationMs: Long,
    progress: @Composable () -> Float,
    waveformBars: List<WaveBar>,
    detail: NowPlayingEpisodeDetail,
    onBack: () -> Unit,
    onOpenDownloads: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekPrev: () -> Unit,
    onSeekNext: () -> Unit,
    onSeekTo: (Float) -> Unit,
) {
    var scrubFraction by remember { mutableStateOf<Float?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }

    // Cache WaveBar list - only recreate when waveformBars actually changes

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(58.dp))

            // Top bar
            Box(modifier = Modifier.fillMaxWidth()) {
                // Back button
                Surface(
                    onClick = onBack,
                    modifier = Modifier
                        .size(37.dp)
                        .align(Alignment.CenterStart),
                    shape = CircleShape,
                    color = Color.Black,
                    border = BorderStroke(0.75.dp, Color(0xFFD9D9D9)),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = IC_BACK,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                // Title
                Text(
                    text = "Now Playing",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                )
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Surface(
                        onClick = { showTopMenu = !showTopMenu },
                        modifier = Modifier.size(37.dp),
                        shape = CircleShape,
                        color = Color.Black,
                        border = BorderStroke(0.75.dp, Color(0xFFD9D9D9)),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "⋮",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Album art with tilted shadow card
            AlbumArtSection(
                imageUrl = imageUrl,
                onClick = { showDetailSheet = true },
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Song info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.56).sp,
                )
                Text(
                    text = artist,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextPrimary,
                    letterSpacing = (-0.32).sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Waveform - show loading animation when generating
            if (isGeneratingWaveform) {
                WaveformLoading(modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                )
            } else {
                AudioWaveform(
                    waveformBars = waveformBars,
                    progress = progress,
                    onSeekTo = onSeekTo,
                    onScrub = { scrubFraction = it },
                    onScrubEnd = { scrubFraction = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                )
            }

            if (scrubFraction != null && !isGeneratingWaveform) {
                val showFraction = scrubFraction ?: progress()
                val showTime = formatTimeFromFraction(showFraction, durationMs)
                Text(
                    text = "${showTime} / ${formatDuration(durationMs)}",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TransportControls(
                isPlaying = isPlaying,
                onSeekPrev = onSeekPrev,
                onTogglePlay = onTogglePlay,
                onSeekNext = onSeekNext,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Decorative dots (top-right area)
        AsyncImage(
            model = DOTS_DECO,
            contentDescription = null,
            modifier = Modifier
                .size(width = 61.dp, height = 53.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-17).dp, y = 110.dp),
        )

        if (showTopMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showTopMenu = false },
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 106.dp, end = 20.dp)
                    .widthIn(min = 150.dp, max = 190.dp),
            ) {
                ShadowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.download_management),
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showTopMenu = false
                                onOpenDownloads()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    )
                }
            }
        }

        if (showDetailSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable { showDetailSheet = false },
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                ShadowCard(modifier = Modifier.fillMaxWidth()) {
                    EpisodeDetailHalfSheet(
                        detail = detail,
                        fallbackCover = imageUrl,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowTime(
    modifier: Modifier,
    showTime: @Composable () -> String,
    durationMs: Long
) {
    Text(
        text = "${showTime()} / ${formatDuration(durationMs)}",
        fontSize = 12.sp,
        color = TextPrimary,
        modifier = modifier.padding(top = 8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun AlbumArtSection(
    imageUrl: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Tilted green card (shadow)
        Box(
            modifier = Modifier
                .size(width = 316.dp, height = 310.dp)
                .rotate(5.14f)
                .offset(x = (-8).dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AccentGreen),
        )
        // Main album art card on top
        Surface(
            modifier = Modifier
                .size(width = 316.dp, height = 310.dp)
                .offset(x = 4.dp, y = 14.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, CardBorder),
            color = AlbumCardBg,
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Album cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun AudioWaveform(
    waveformBars: List<WaveBar>,
    progress: @Composable () -> Float,
    onSeekTo: (Float) -> Unit,
    onScrub: (Float) -> Unit,
    onScrubEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val barWidthPx = with(density) { 4.dp.toPx() }
    val spacingPx = with(density) { 3.dp.toPx() }

    val currentProgressProvider by rememberUpdatedState(progress)
    val barList = waveformBars

    val animatedProgress by animateFloatAsState(
        targetValue = currentProgressProvider(),
        animationSpec = tween(durationMillis = 120, easing = LinearEasing),
        label = "waveformProgress",
    )

    Canvas(
        modifier = modifier.pointerInput(Unit) {

            fun updateSeekPosition(
                touchX: Float,
                currentProgress: Float,
            ) {
                val totalBars = barList.size
                if (totalBars == 0) return
                val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
                val playheadX = currentProgress.coerceIn(0f, 1f) * totalWidth
                val centerX = size.width / 2f
                val minOffset = size.width - totalWidth
                val desiredOffset = centerX - playheadX
                val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)
                val positionX = (touchX - offsetX).coerceIn(0f, totalWidth)
                val fraction = (positionX / totalWidth).coerceIn(0f, 1f)
                onSeekTo(fraction)
                onScrub(fraction)
            }

            detectDragGestures(
                onDragStart = { offset ->
                    updateSeekPosition(offset.x, animatedProgress)
                },
                onDrag = { change, _ ->
                    updateSeekPosition(change.position.x, animatedProgress)
                },
                onDragEnd = { onScrubEnd() },
                onDragCancel = { onScrubEnd() },
            )
        }
    ) {

        val currentProgress = animatedProgress

        val totalBars = barList.size
        if (totalBars == 0) return@Canvas
        val maxHeightPx = size.height
        val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
        val clampedProgress = currentProgress.coerceIn(0f, 1f)
        val playheadX = clampedProgress * totalWidth

        val centerX = size.width / 2f
        val minOffset = size.width - totalWidth
        val desiredOffset = centerX - playheadX
        val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)

        barList.forEachIndexed { index, bar ->
            val absoluteX = index * (barWidthPx + spacingPx)
            val x = absoluteX + offsetX
            val barH = (bar.height * maxHeightPx * 1.2f).coerceAtMost(maxHeightPx)
            val y = (maxHeightPx - barH) / 2f
            // 计算当前这条 Bar 的左右边界（相对于音频总长度的绝对坐标）
            val barLeft = absoluteX
            val barRight = absoluteX + barWidthPx

            when {
                // 情况 1: 播放头已经完全覆盖该 Bar -> 全绿
                barRight <= playheadX -> {
                    drawRoundRect(
                        color = AccentGreen,
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }
                // 情况 2: 播放头还没到该 Bar -> 全灰
                barLeft >= playheadX -> {
                    drawRoundRect(
                        color = WaveGray,
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }
                // 情况 3: 播放头正处于该 Bar 中间 -> 丝滑渐变点亮
                else -> {
                    // 计算播放头在该 Bar 内部的相对比例 (0.0 ~ 1.0)
                    val internalProgress = (playheadX - barLeft) / barWidthPx

                    // 使用 Brush.linearGradient 实现左绿右灰的硬切分
                    // 通过 stop 相同来实现没有模糊的平滑切割点
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            0f to AccentGreen,
                            internalProgress to AccentGreen,
                            internalProgress to WaveGray,
                            1f to WaveGray,
                            startX = x,
                            endX = x + barWidthPx
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }
            }
        }
    }
}

private fun formatTimeFromFraction(fraction: Float, durationMs: Long): String {
    val clamped = fraction.coerceIn(0f, 1f)
    val totalSeconds = ((durationMs / 1000f) * clamped).roundToInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000f).roundToInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun TransportControls(
    isPlaying: @Composable () -> Boolean,
    onSeekPrev: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekNext: () -> Unit,
) {

    val playing = isPlaying()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = onSeekPrev,
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = IC_PREV,
                    contentDescription = stringResource(R.string.cd_back_10s),
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(180f),
                )
            }
        }

        Surface(
            onClick = onTogglePlay,
            modifier = Modifier.size(66.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playing) {
                        stringResource(R.string.cd_pause)
                    } else {
                        stringResource(R.string.cd_play)
                    },
                    tint = TextPrimary,
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        Surface(
            onClick = onSeekNext,
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = IC_NEXT,
                    contentDescription = stringResource(R.string.cd_forward_30s),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun EpisodeDetailHalfSheet(
    detail: NowPlayingEpisodeDetail,
    fallbackCover: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 320.dp, max = 520.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = detail.imageUrl ?: fallbackCover,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = detail.title.ifBlank { stringResource(R.string.episode_details) },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                detail.pubDate?.let {
                    Text(
                        text = stringResource(R.string.published_format, it),
                        fontSize = 12.sp,
                        color = Color(0xFF6B6B6B),
                    )
                }
            }
        }

        if (detail.annotatedDesc != null) {
            Text(
                text = detail.annotatedDesc,
                fontSize = 14.sp,
                color = Color(0xFF343434),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            )
        } else {
            Text(
                text = stringResource(R.string.no_details_available),
                fontSize = 14.sp,
                color = Color(0xFF343434),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}

@Composable
private fun WaveformLoading(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    // Base random heights - fixed random pattern
    val baseHeights = remember {
        List(50) { i ->
            // Use position-based pseudo-random for consistency
            val noise = Random(i).nextFloat()
            0.25f + noise * 0.45f // 25% ~ 70% base height range
        }
    }

    val dp8 = with(LocalDensity.current) { 8.dp.toPx() }
    // Base random vertical offsets
    val baseOffsets = remember {
        List(50) { i ->
            val noise = Random(i + 1000).nextFloat()
            (noise - 0.5f) * dp8 // -4dp ~ +4dp static random
        }
    }

    Canvas(modifier = modifier) {
        val barWidthPx = 4.dp.toPx()
        val spacingPx = 3.dp.toPx()
        val maxHeight = size.height

        repeat(50) { i ->
            val x = i * (barWidthPx + spacingPx)
            // Phase offset increases from left to right
            // time - phase creates wave traveling to the right
            // left bars move first, right bars follow - correct direction
            val phase = (i / 50f) * 2 * kotlin.math.PI.toFloat()
            // Each bar height animates independently - looks like sound wave moving right
            val heightNorm = baseHeights[i] + 0.2f * kotlin.math.sin(time - phase)
            val barHeight = heightNorm.coerceIn(0.15f, 0.85f) * maxHeight
            val baseY = (maxHeight - barHeight) / 2f
            val y = baseY + baseOffsets[i]

            // Constant alpha - height animation creates the wave movement effect
            drawRoundRect(
                color = WaveGray.copy(alpha = 0.5f),
                topLeft = Offset(x, y),
                size = Size(barWidthPx, barHeight),
                cornerRadius = CornerRadius(barWidthPx / 2f)
            )
        }
    }
}

