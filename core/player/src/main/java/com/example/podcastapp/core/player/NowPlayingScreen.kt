package com.example.podcastapp.core.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

// --- Design tokens ---
private val ScreenBg        = Color(0xFFFFFFED)  // #FFFFED
private val AccentGreen     = Color(0xFF87B800)
private val WaveGray        = Color(0xFFC4C4C4)
private val TextPrimary     = Color(0xFF000000)
private val CardBorder      = Color(0xFF000000)
private val ShadowColor     = Color(0x40000000)
private val AlbumCardBg     = Color(0xFFFFFFFF)

// --- Waveform data (normalised 0..1, derived from Figma pixel heights, max=66.3px) ---
// true = played (green), false = unplayed (gray)
private data class WaveBar(val height: Float, val played: Boolean)

private val WAVEFORM: List<WaveBar> = listOf(
    WaveBar(0.508f, true), WaveBar(0.869f, true), WaveBar(0.738f, true),
    WaveBar(0.377f, true), WaveBar(0.508f, true), WaveBar(0.410f, true),
    WaveBar(1.000f, true), WaveBar(0.508f, true), WaveBar(0.508f, true),
    WaveBar(0.508f, true), WaveBar(0.344f, true), WaveBar(0.443f, true),
    WaveBar(0.180f, true), WaveBar(0.443f, true), WaveBar(0.934f, true),
    WaveBar(0.639f, true), WaveBar(0.443f, true), WaveBar(0.279f, true),
    WaveBar(0.639f, true), WaveBar(0.934f, true), WaveBar(0.639f, true),
    WaveBar(0.639f, true), WaveBar(0.738f, true), WaveBar(0.934f, true),
    WaveBar(0.574f, true), WaveBar(0.443f, true), WaveBar(0.639f, true),
    WaveBar(0.639f, true), WaveBar(0.508f, true),
    // -- unplayed --
    WaveBar(0.869f, false), WaveBar(0.738f, false), WaveBar(0.574f, false),
    WaveBar(0.508f, false), WaveBar(0.639f, false), WaveBar(1.000f, false),
    WaveBar(0.508f, false), WaveBar(0.508f, false), WaveBar(0.508f, false),
    WaveBar(0.246f, false), WaveBar(0.312f, false), WaveBar(0.115f, false),
    WaveBar(0.312f, false), WaveBar(0.639f, false), WaveBar(0.443f, false),
    WaveBar(0.312f, false), WaveBar(0.180f, false), WaveBar(0.443f, false),
    WaveBar(0.639f, false), WaveBar(0.443f, false), WaveBar(0.443f, false),
    WaveBar(0.508f, false), WaveBar(0.639f, false), WaveBar(0.410f, false),
    WaveBar(0.312f, false), WaveBar(0.443f, false), WaveBar(0.443f, false),
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
    NowPlayingScreen(
        title     = state.title.ifBlank { "Take My Breath" },
        artist    = state.artist ?: "The Weeknd",
        imageUrl  = state.imageUrl ?: ALBUM_COVER,
        isPlaying = state.isPlaying,
        progress  = if (state.durationMs > 0) {
            (state.positionMs.toFloat() / state.durationMs).coerceIn(0f, 1f)
        } else 0.518f,
        onBack          = onBack,
        onTogglePlay    = { viewModel.togglePlayPause(state.isPlaying) },
        onSeekPrev      = { viewModel.seekTo(maxOf(0, state.positionMs - 15_000)) },
        onSeekNext      = { viewModel.seekTo(minOf(state.durationMs, state.positionMs + 15_000)) },
    )
}

@Composable
fun NowPlayingScreen(
    title: String,
    artist: String,
    imageUrl: String,
    isPlaying: Boolean,
    progress: Float,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekPrev: () -> Unit,
    onSeekNext: () -> Unit,
) {
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
                bars = WAVEFORM,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
            )

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
private fun AudioWaveform(bars: List<WaveBar>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val totalBars  = bars.size
        val barWidthPx = 4.35f * density
        val spacingPx  = (size.width - totalBars * barWidthPx) / (totalBars - 1)
        val maxHeightPx = size.height

        bars.forEachIndexed { index, bar ->
            val x    = index * (barWidthPx + spacingPx)
            val barH = bar.height * maxHeightPx
            val y    = (maxHeightPx - barH) / 2f
            drawRoundRect(
                color        = if (bar.played) Color(0xFF87B800) else Color(0xFFC4C4C4),
                topLeft      = Offset(x, y),
                size         = Size(barWidthPx, barH),
                cornerRadius = CornerRadius(barWidthPx / 2f),
            )
        }
    }
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
