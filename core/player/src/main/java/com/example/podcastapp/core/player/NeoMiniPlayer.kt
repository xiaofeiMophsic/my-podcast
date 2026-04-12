package com.example.podcastapp.core.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.theme.PodcastTheme
import com.example.podcastapp.core.ui.utils.toPlaybackString
import com.example.podcastapp.feature.player.R
import kotlin.time.Duration.Companion.milliseconds

//todo 进度不走，无法暂停，标题需要跑马灯
@Composable
fun NeoMiniPlayer(
    title: String,
    progressMs: () -> Long,
    durationMs: Long,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPlayListClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .dropShadow(
                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp), shadow = Shadow(
                    radius = 6.dp, color = Color(0x1A000000), offset = DpOffset(0.dp, (-4).dp)
                )
            ), color = Color.White, shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                PlayProgressText(
                    durationMs = durationMs,
                    progressMs = progressMs
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onPlayPauseClick),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    progress = {
                        if (durationMs > 0) (progressMs().toFloat() / durationMs) else 0f
                    },
                    modifier = Modifier.size(40.dp),
                    color = NeoColors.AccentGreen,
                    strokeWidth = 2.dp,
                    trackColor = NeoColors.OnSecondary,
                )
                Image(
                    painter =
                        if (isPlaying)
                            painterResource(id = R.drawable.miniplayer_pause)
                        else painterResource(id = R.drawable.miniplayer_play),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x3387B800))
                    .clickable(onClick = onPlayListClick), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.miniplayer_list),
                    contentDescription = "Queue",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayProgressText(durationMs: Long, progressMs: () -> Long) {
    val progressSeconds by remember {
        derivedStateOf {
            progressMs().milliseconds / 1000
        }
    }

    val displayText by remember {
        derivedStateOf {
            val currentStr = progressSeconds.toPlaybackString()
            val totalStr = durationMs.milliseconds.toPlaybackString()
            "$currentStr / $totalStr"
        }
    }

    Text(
        text = displayText,
        style = MaterialTheme.typography.bodySmall,
        color = NeoColors.LightGray
    )
}

@Preview(showBackground = true)
@Composable
fun NeoMiniPlayerPreview() {
    PodcastTheme {
        NeoMiniPlayer(
            title = stringResource(id = R.string.sample_track_title),
            progressMs = { 225000 },
            durationMs = 2700000,
            isPlaying = false,
            onPlayPauseClick = {},
            onPlayListClick = {},
        )
    }
}
