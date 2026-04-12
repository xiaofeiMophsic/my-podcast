package com.example.podcastapp.core.player

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.podcastapp.core.player.wave.AudioWaveform
import com.example.podcastapp.core.player.wave.WaveformLoading
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard
import com.example.podcastapp.core.ui.utils.toPlaybackString
import com.example.podcastapp.feature.player.R
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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
    viewModel: PlayerViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val state by viewModel.metadataState.collectAsStateWithLifecycle()
    val detail by viewModel.episodeDetail.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingWaveform.collectAsStateWithLifecycle()
    val waveformBars by viewModel.waveformBars.collectAsStateWithLifecycle()

    val playingState = viewModel.playingState.collectAsStateWithLifecycle()
    val progressState = viewModel.progressState.collectAsStateWithLifecycle()

    LaunchedEffect(targetEpisodeId) {
        targetEpisodeId?.let { viewModel.playEpisodeById(it) }
    }

    NowPlayingScreen(
        title = state.title.ifBlank { "Take My Breath" },
        artist = state.artist ?: "The Weeknd",
        imageUrl = state.imageUrl ?: ALBUM_COVER,
        isGeneratingWaveform = isGenerating,
        isPlaying = { playingState.value },
        durationMs = state.durationMs,
        progress = {
            val currentProgress = progressState.value
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
    isPlaying: () -> Boolean,
    durationMs: Long,
    progress: () -> Float,
    waveformBars: FloatArray,
    detail: NowPlayingEpisodeDetail,
    onBack: () -> Unit,
    onOpenDownloads: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekPrev: () -> Unit,
    onSeekNext: () -> Unit,
    onSeekTo: (Float) -> Unit,
) {
    val scrubFraction = remember { mutableStateOf<Float?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }

    // Cache WaveBar list - only recreate when waveformBars actually changes

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NeoTopBar(
                title = "Now Playing",
                onBack = onBack,
            )
            // Top bar
//            Box(modifier = Modifier.fillMaxWidth()) {
//                // Back button
//                Surface(
//                    onClick = onBack,
//                    modifier = Modifier
//                        .size(37.dp)
//                        .align(Alignment.CenterStart),
//                    shape = CircleShape,
//                    color = Color.Black,
//                    border = BorderStroke(0.75.dp, Color(0xFFD9D9D9)),
//                ) {
//                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
//                        AsyncImage(
//                            model = IC_BACK,
//                            contentDescription = "Back",
//                            modifier = Modifier.size(20.dp),
//                        )
//                    }
//                }
//                // Title
//                Text(
//                    text = "Now Playing",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = TextPrimary,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.align(Alignment.Center),
//                )
//                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
//                    Surface(
//                        onClick = { showTopMenu = !showTopMenu },
//                        modifier = Modifier.size(37.dp),
//                        shape = CircleShape,
//                        color = Color.Black,
//                        border = BorderStroke(0.75.dp, Color(0xFFD9D9D9)),
//                    ) {
//                        Box(
//                            contentAlignment = Alignment.Center,
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            Text(
//                                text = "⋮",
//                                color = Color.White,
//                                fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                            )
//                        }
//                    }
//                }
//            }

            Spacer(modifier = Modifier.height(28.dp))

            // Album art with tilted shadow card
            AlbumArtSection(
                imageUrl = imageUrl,
                onClick = { showDetailSheet = true },
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Song info
            EpisodeTitle(title, artist)

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
                    onScrub = { scrubFraction.value = it },
                    onScrubEnd = { scrubFraction.value = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                )
            }

            PlayingProgressText({ scrubFraction.value }, progress, durationMs)

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
private fun EpisodeTitle(title: String, artist: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
        )
    }
}

@Composable
private fun ColumnScope.PlayingProgressText(
    scrubFractionProvider: () -> Float?,
    progress: () -> Float,
    durationMs: Long
) {

    val showTimeSeconds by remember(durationMs) {
        derivedStateOf {
            val showFraction = scrubFractionProvider() ?: progress()
            (durationMs * showFraction.coerceIn(0f, 1f) / 1000).toLong()
        }
    }

    val showTimeText = showTimeSeconds.seconds.toPlaybackString()
    Text(
        text = "$showTimeText / ${durationMs.milliseconds.toPlaybackString()}",
        fontSize = 12.sp,
        color = TextPrimary,
        modifier = Modifier
            .padding(top = 8.dp)
            .align(Alignment.CenterHorizontally),
        maxLines = 1,
    )
}

@Composable
private fun AlbumArtSection(
    imageUrl: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Tilted green card (shadow)
        Box(
            modifier = Modifier
                .size(width = 310.dp, height = 310.dp)
                .rotate(5.14f)
                .clip(RoundedCornerShape(24.dp))
                .background(AccentGreen)
                .border(BorderStroke(1.dp, CardBorder)),
        )
        // Main album art card on top
        Surface(
            modifier = Modifier
                .size(width = 310.dp, height = 310.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, CardBorder),
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
private fun TransportControls(
    isPlaying: () -> Boolean,
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
