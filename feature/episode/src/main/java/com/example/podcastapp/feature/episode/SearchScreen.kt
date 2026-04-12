package com.example.podcastapp.feature.episode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoShapes
import com.example.podcastapp.core.ui.neo.NeoTextField
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard
import com.example.podcastapp.core.ui.utils.htmlToAnnotatedString

@Composable
fun SearchRoute(
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsState()
    val results = viewModel.results.collectAsLazyPagingItems()
    val isEmptyQuery by viewModel.isEmptyQuery.collectAsState()
    val history by viewModel.history.collectAsState()

    SearchScreen(
        query = query,
        isEmptyQuery = isEmptyQuery,
        onQueryChange = viewModel::updateQuery,
        onClearHistory = viewModel::clearHistory,
        history = history,
        results = results,
        onEpisodeClick = onEpisodeClick,
        onBack = onBack,
    )
}

@Composable
private fun HistoryChips(
    items: List<String>,
    onClick: (String) -> Unit,
    onClear: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recent searches",
                fontSize = 12.sp,
                color = NeoColors.TextSecondary,
            )
            Text(
                text = "Clear",
                fontSize = 12.sp,
                color = NeoColors.TextSecondary,
                modifier = Modifier.clickable { onClear() },
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(items) { label ->
                Surface(
                    shape = NeoShapes.Chip,
                    color = NeoColors.CardBg,
                    border = BorderStroke(0.75.dp, NeoColors.CardBorder),
                    modifier = Modifier
                        .height(32.dp)
                        .clickable { onClick(label) },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = NeoColors.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    query: String,
    isEmptyQuery: Boolean,
    onQueryChange: (String) -> Unit,
    onClearHistory: () -> Unit,
    history: List<String>,
    results: LazyPagingItems<EpisodeEntity>,
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoColors.ScreenBg)
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(title = "Search", onBack = onBack)
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NeoTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = "Search episodes",
                    modifier = Modifier.fillMaxWidth(),
                    inputModifier = Modifier.focusRequester(focusRequester),
                )
                AnimatedVisibility(visible = isEmptyQuery) {
                    Text(
                        text = "Enter a keyword to search",
                        fontSize = 12.sp,
                        color = NeoColors.TextSecondary,
                    )
                }
                if (history.isNotEmpty()) {
                    HistoryChips(
                        items = history,
                        onClick = onQueryChange,
                        onClear = onClearHistory,
                    )
                }
            }

            if (!isEmptyQuery) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        count = results.itemCount,
                        key = results.itemKey { it.id },
                    ) { index ->
                        val item = results[index]
                        if (item != null) {
                            ShadowCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEpisodeClick(item.id) }
                                    .animateContentSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoColors.TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    item.description?.let {
                                        Text(
                                            text = it,
                                            fontSize = 13.sp,
                                            color = NeoColors.TextSecondary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Text(
                                        text = "Open",
                                        fontSize = 12.sp,
                                        color = NeoColors.AccentGreen,
                                        modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
