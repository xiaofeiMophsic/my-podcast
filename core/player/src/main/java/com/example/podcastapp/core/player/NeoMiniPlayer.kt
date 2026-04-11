package com.example.podcastapp.core.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.podcastapp.feature.player.R
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.theme.PodcastTheme

@Composable
fun NeoMiniPlayer(
    title: String,
    progressText: String,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPlayListClick: () -> Unit,
    modifier: Modifier = Modifier,
    playbackProgress: Float = 0f,
    imageUrl: String? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.White,
        shadowElevation = 8.dp
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onPlayPauseClick),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    progress = { playbackProgress },
                    modifier = Modifier.size(40.dp),
                    color = NeoColors.AccentGreen,
                    strokeWidth = 2.dp,
                    trackColor = NeoColors.OnSecondary,
                )
                Image(
                    painter = painterResource(id = R.drawable.miniplayer_play),
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
                    .clickable(onClick = onPlayListClick),
                contentAlignment = Alignment.Center
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

@Preview(showBackground = true)
@Composable
fun NeoMiniPlayerPreview() {
    PodcastTheme {
        NeoMiniPlayer(
            title = stringResource(id = R.string.sample_track_title),
            progressText = stringResource(id = R.string.sample_track_progress),
            isPlaying = false,
            onPlayPauseClick = {},
            onPlayListClick = {},
            playbackProgress = 0.3f
        )
    }
}
