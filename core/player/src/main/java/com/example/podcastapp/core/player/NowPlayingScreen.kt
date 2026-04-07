package com.example.podcastapp.core.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlin.math.roundToInt

// --- Design tokens ---
private val ScreenBg        = Color(0xFFFFFFED)  // #FFFFED
private val AccentGreen     = Color(0xFF87B800)
private val WaveGray        = Color(0xFFC4C4C4)
private val TextPrimary     = Color(0xFF000000)
private val CardBorder      = Color(0xFF000000)
private val ShadowColor     = Color(0x40000000)
private val AlbumCardBg     = Color(0xFFFFFFFF)

// --- Waveform data (normalised 0..1, derived from Figma pixel heights, max=66.3px) ---
private data class WaveBar(val height: Float)

private val WAVEFORM: List<WaveBar> = listOf(
    WaveBar(0.508f), WaveBar(0.869f), WaveBar(0.738f),
    WaveBar(0.377f), WaveBar(0.508f), WaveBar(0.410f),
    WaveBar(1.000f), WaveBar(0.508f), WaveBar(0.508f),
    WaveBar(0.508f), WaveBar(0.344f), WaveBar(0.443f),
    WaveBar(0.180f), WaveBar(0.443f), WaveBar(0.934f),
    WaveBar(0.639f), WaveBar(0.443f), WaveBar(0.279f),
    WaveBar(0.639f), WaveBar(0.934f), WaveBar(0.639f),
    WaveBar(0.639f), WaveBar(0.738f), WaveBar(0.934f),
    WaveBar(0.574f), WaveBar(0.443f), WaveBar(0.639f),
    WaveBar(0.639f), WaveBar(0.508f),
    WaveBar(0.869f), WaveBar(0.738f), WaveBar(0.574f),
    WaveBar(0.508f), WaveBar(0.639f), WaveBar(1.000f),
    WaveBar(0.508f), WaveBar(0.508f), WaveBar(0.508f),
    WaveBar(0.246f), WaveBar(0.312f), WaveBar(0.115f),
    WaveBar(0.312f), WaveBar(0.639f), WaveBar(0.443f),
    WaveBar(0.312f), WaveBar(0.180f), WaveBar(0.443f),
    WaveBar(0.639f), WaveBar(0.443f), WaveBar(0.443f),
    WaveBar(0.508f), WaveBar(0.639f), WaveBar(0.410f),
    WaveBar(0.312f), WaveBar(0.443f), WaveBar(0.443f),
)

// Asset URLs (Figma MCP, valid 7 days)
private const val ALBUM_COVER  = "https://www.figma.com/api/mcp/asset/6ee017b6-7ee4-4703-bf43-f4d9ad27bafd"
private const val DOTS_DECO    = "https://www.figma.com/api/mcp/asset/23cb03d4-760f-433f-abf3-022b992aa0e8"
private const val IC_BACK      = "https://www.figma.com/api/mcp/asset/000d493b-3ea5-47a6-b2d1-9f9a35b6109c"
private const val IC_HEART     = "https://www.figma.com/api/mcp/asset/0f849085-4cba-46aa-945c-b1e38fc2bf11"
private const val IC_REPEAT    = "https://www.figma.com/api/mcp/asset/2c558165-f0fb-47b5-94b0-46b833787304"
private const val IC_PREV      = "https://www.figma.com/api/mcp/asset/430f9f9f-5824-42b3-8bc9-7d41811abed7"
private const val IC_PLAY      = "https://www.figma.com/api/mcp/asset/a41687f2-4e16-41fb-8641-d5a55b88bb8a"
private const val IC_NEXT      = "https://www.figma.com/api/mcp/asset/58602731-3143-4af5-83c6-7cd52b71b5b7"
private const val IC_MORE      = "https://www.figma.com/api/mcp/asset/94acd51b-3167-4226-95d6-1f7fccf44851"

@Composable
fun NowPlayingRoute(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val progress by remember {
        derivedStateOf {
            if (state.durationMs > 0) {
                (state.positionMs.toFloat() / state.durationMs).coerceIn(0f, 1f)
            } else 0.518f
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 120, easing = LinearEasing),
        label = "waveformProgress",
    )

    NowPlayingScreen(
        title     = state.title.ifBlank { "Take My Breath" },
        artist    = state.artist ?: "The Weeknd",
        imageUrl  = state.imageUrl ?: ALBUM_COVER,
        isPlaying = state.isPlaying,
        durationMs = state.durationMs,
        progress  = animatedProgress,
        waveformBars = state.waveformBars,
        onBack          = onBack,
        onTogglePlay    = { viewModel.togglePlayPause(state.isPlaying) },
        onSeekPrev      = { viewModel.seekTo(maxOf(0, state.positionMs - 15_000)) },
        onSeekNext      = { viewModel.seekTo(minOf(state.durationMs, state.positionMs + 15_000)) },
        onSeekTo        = { fraction ->
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
    isPlaying: Boolean,
    durationMs: Long,
    progress: Float,
    waveformBars: List<Float>,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekPrev: () -> Unit,
    onSeekNext: () -> Unit,
    onSeekTo: (Float) -> Unit,
) {
    var scrubFraction by remember { mutableStateOf<Float?>(null) }
    val showFraction = scrubFraction ?: progress
    val showTime = formatTimeFromFraction(showFraction, durationMs)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
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
                // Heart icon
                AsyncImage(
                    model = IC_HEART,
                    contentDescription = "Favourite",
                    modifier = Modifier
                        .size(37.dp)
                        .align(Alignment.CenterEnd),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Album art with tilted shadow card
            AlbumArtSection(imageUrl = imageUrl)

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

            // Waveform
            AudioWaveform(
                bars = if (waveformBars.isNotEmpty()) waveformBars.map { WaveBar(it) } else WAVEFORM,
                progress = progress,
                onSeekTo = onSeekTo,
                onScrub = { scrubFraction = it },
                onScrubEnd = { scrubFraction = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
            )

            if (scrubFraction != null) {
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

            // Playback controls
            PlaybackControls(
                isPlaying = isPlaying,
                onRepeat   = {},
                onPrev     = onSeekPrev,
                onPlay     = onTogglePlay,
                onNext     = onSeekNext,
                onMore     = {},
            )

            Spacer(modifier = Modifier.height(32.dp))

            // View Lyrics button
            Button(
                onClick = {},
                modifier = Modifier
                    .width(234.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                border = BorderStroke(1.dp, Color.White),
            ) {
                Text(
                    text = "View Lyrics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
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
    }
}

@Composable
private fun AlbumArtSection(imageUrl: String) {
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
                .offset(x = 4.dp, y = 14.dp),
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
    bars: List<WaveBar>,
    progress: Float,
    onSeekTo: (Float) -> Unit,
    onScrub: (Float) -> Unit,
    onScrubEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val barWidthPx = with(density) { 4.dp.toPx() }
    val spacingPx = with(density) { 3.dp.toPx() }

    Canvas(
        modifier = modifier.pointerInput(bars, progress) {
            detectDragGestures(
                onDragStart = { offset ->
                    val totalBars = bars.size
                    if (totalBars == 0) return@detectDragGestures
                    val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
                    val playheadX = progress.coerceIn(0f, 1f) * totalWidth
                    val centerX = size.width / 2f
                    val minOffset = size.width - totalWidth
                    val desiredOffset = centerX - playheadX
                    val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)
                    val positionX = (offset.x - offsetX).coerceIn(0f, totalWidth)
                    val fraction = (positionX / totalWidth).coerceIn(0f, 1f)
                    onSeekTo(fraction)
                    onScrub(fraction)
                },
                onDrag = { change, _ ->
                    val totalBars = bars.size
                    if (totalBars == 0) return@detectDragGestures
                    val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
                    val playheadX = progress.coerceIn(0f, 1f) * totalWidth
                    val centerX = size.width / 2f
                    val minOffset = size.width - totalWidth
                    val desiredOffset = centerX - playheadX
                    val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)
                    val positionX = (change.position.x - offsetX).coerceIn(0f, totalWidth)
                    val fraction = (positionX / totalWidth).coerceIn(0f, 1f)
                    onSeekTo(fraction)
                    onScrub(fraction)
                },
                onDragEnd = { onScrubEnd() },
                onDragCancel = { onScrubEnd() },
            )
        }
    ) {
        val totalBars = bars.size
        if (totalBars == 0) return@Canvas
        val maxHeightPx = size.height
        val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
        val clampedProgress = progress.coerceIn(0f, 1f)
        val playheadX = clampedProgress * totalWidth

        val centerX = size.width / 2f
        val minOffset = size.width - totalWidth
        val desiredOffset = centerX - playheadX
        val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)

        bars.forEachIndexed { index, bar ->
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
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
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
    return String.format("%d:%02d", minutes, seconds)
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onRepeat: () -> Unit,
    onPrev: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onMore: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = IC_REPEAT,
            contentDescription = "Repeat",
            modifier = Modifier.size(29.dp),
        )
        // Prev = next icon rotated 180°
        AsyncImage(
            model = IC_PREV,
            contentDescription = "Previous",
            modifier = Modifier
                .size(37.dp)
                .rotate(180f),
        )
        // Play/Pause button
        Surface(
            onClick = onPlay,
            modifier = Modifier.size(66.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = IC_PLAY,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(36.dp),
                )
            }
        }
        AsyncImage(
            model = IC_NEXT,
            contentDescription = "Next",
            modifier = Modifier.size(37.dp),
        )
        AsyncImage(
            model = IC_MORE,
            contentDescription = "More",
            modifier = Modifier.size(32.dp),
        )
    }
}
