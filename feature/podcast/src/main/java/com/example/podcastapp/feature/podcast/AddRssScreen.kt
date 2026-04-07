package com.example.podcastapp.feature.podcast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoPrimaryButton
import com.example.podcastapp.core.ui.neo.NeoTextField
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.NeoOutlineButton
import androidx.compose.material3.Text
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AddRssRoute(
    onBack: () -> Unit,
    viewModel: PodcastListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.addFeedEvents.collect { event ->
            val message = when (event) {
                is AddFeedEvent.Success -> event.message
                is AddFeedEvent.Failure -> event.message
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
        }
    }
    AddRssScreen(
        state = state,
        onAddFeed = viewModel::addSubscription,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun AddRssScreen(
    state: PodcastListUiState,
    onAddFeed: (String) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    var feedUrl by remember { mutableStateOf("") }
    val transition = rememberInfiniteTransition(label = "adding")
    val loadingAlpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "loadingAlpha",
    )

    Scaffold(
        containerColor = NeoColors.ScreenBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 8.dp)
                .background(NeoColors.ScreenBg),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                NeoTopBar(title = "Add RSS", onBack = onBack)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    NeoTextField(
                        value = feedUrl,
                        onValueChange = { feedUrl = it },
                        placeholder = "Paste RSS feed URL",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading,
                    )
                    Text(
                        text = "Tip: Use the feed URL from your podcast provider.",
                        color = NeoColors.TextSecondary,
                        fontSize = 12.sp,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NeoPrimaryButton(
                            text = if (state.isLoading) "Adding…" else "Subscribe",
                            onClick = { onAddFeed(feedUrl) },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (state.isLoading) loadingAlpha else 1f),
                        )
                        NeoOutlineButton(
                            text = "Clear",
                            onClick = { feedUrl = "" },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading,
                        )
                    }
                    AnimatedVisibility(visible = state.errorMessage != null) {
                        Text(
                            text = state.errorMessage.orEmpty(),
                            color = NeoColors.TextSecondary,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}
