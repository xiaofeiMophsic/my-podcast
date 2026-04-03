package com.example.podcastapp.feature.podcast

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun PodcastListRoute(
    onPodcastClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    viewModel: PodcastListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    PodcastListScreen(
        state = state,
        onAddFeed = viewModel::addSubscription,
        onRefreshPodcast = viewModel::refreshPodcast,
        onPodcastClick = onPodcastClick,
        onSearchClick = onSearchClick,
        onDownloadsClick = onDownloadsClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastListScreen(
    state: PodcastListUiState,
    onAddFeed: (String) -> Unit,
    onRefreshPodcast: (String) -> Unit,
    onPodcastClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onDownloadsClick: () -> Unit,
) {
    var feedUrl by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Subscriptions") },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                }
                IconButton(onClick = onDownloadsClick) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Downloads",
                    )
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = feedUrl,
                onValueChange = { feedUrl = it },
                label = { Text("Add RSS feed URL") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    onAddFeed(feedUrl)
                    feedUrl = ""
                },
                enabled = !state.isLoading,
            ) {
                Text(if (state.isLoading) "Adding..." else "Subscribe")
            }

            state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.items) { item ->
                PodcastRow(
                    item = item,
                    onPodcastClick = onPodcastClick,
                    onRefresh = { onRefreshPodcast(item.feedUrl) },
                )
            }
        }
    }
}

@Composable
private fun PodcastRow(
    item: PodcastUi,
    onPodcastClick: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = { onPodcastClick(item.id) }) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (item.isSubscribed) {
                        Text(
                            text = "Subscribed",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                IconButton(
                    onClick = onRefresh,
                    enabled = !item.isRefreshing,
                ) {
                    val rotation = remember { Animatable(0f) }
                    LaunchedEffect(item.isRefreshing) {
                        if (item.isRefreshing) {
                            while (true) {
                                rotation.animateTo(
                                    targetValue = rotation.value + 360f,
                                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                                )
                            }
                        } else {
                            if (rotation.value > 0f) {
                                val target = ((rotation.value / 360f).toInt() + 1) * 360f
                                rotation.animateTo(
                                    targetValue = target,
                                    animationSpec = tween(700, easing = FastOutSlowInEasing)
                                )
                                rotation.snapTo(0f)
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.rotate(rotation.value)
                    )
                }
            }
            item.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
