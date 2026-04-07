package com.example.podcastapp.feature.podcast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoOutlineButton
import com.example.podcastapp.core.ui.neo.NeoPrimaryButton
import com.example.podcastapp.core.ui.neo.NeoTextField
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard

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
        onPodcastClick = onPodcastClick,
        onSearchClick = onSearchClick,
        onDownloadsClick = onDownloadsClick,
    )
}

@Composable
fun PodcastListScreen(
    state: PodcastListUiState,
    onAddFeed: (String) -> Unit,
    onPodcastClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onDownloadsClick: () -> Unit,
) {
    var feedUrl by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().padding(bottom = 8.dp).background(NeoColors.ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(
                title = "Subscriptions",
                action = {
                    Row {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = NeoColors.TextPrimary)
                        }
                        IconButton(onClick = onDownloadsClick) {
                            Icon(Icons.Default.Download, contentDescription = "Downloads", tint = NeoColors.TextPrimary)
                        }
                    }
                },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NeoTextField(
                    value = feedUrl,
                    onValueChange = { feedUrl = it },
                    placeholder = "Add RSS feed URL",
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeoPrimaryButton(
                        text = if (state.isLoading) "Adding…" else "Subscribe",
                        onClick = {
                            onAddFeed(feedUrl)
                            feedUrl = ""
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.weight(1f),
                    )
                    NeoOutlineButton(
                        text = "Clear",
                        onClick = { feedUrl = "" },
                        modifier = Modifier.weight(1f),
                    )
                }
                AnimatedVisibility(visible = state.errorMessage != null) {
                    Text(text = state.errorMessage.orEmpty(), color = NeoColors.TextSecondary, fontSize = 12.sp)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.items) { item ->
                    ShadowCard(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoColors.TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = if (item.isSubscribed) "Subscribed" else "",
                                    fontSize = 12.sp,
                                    color = NeoColors.AccentGreen,
                                )
                            }
                            item.description?.let {
                                Text(
                                    text = it,
                                    fontSize = 13.sp,
                                    color = NeoColors.TextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            NeoOutlineButton(
                                text = "Open",
                                onClick = { onPodcastClick(item.id) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}
