package com.example.podcastapp.feature.episode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoTextField
import com.example.podcastapp.core.ui.neo.NeoTopBar
import com.example.podcastapp.core.ui.neo.ShadowCard

@Composable
fun SearchRoute(
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsState()
    val results = viewModel.results.collectAsLazyPagingItems()
    val isEmptyQuery by viewModel.isEmptyQuery.collectAsState()

    SearchScreen(
        query = query,
        isEmptyQuery = isEmptyQuery,
        onQueryChange = viewModel::updateQuery,
        results = results,
        onEpisodeClick = onEpisodeClick,
        onBack = onBack,
    )
}

@Composable
fun SearchScreen(
    query: String,
    isEmptyQuery: Boolean,
    onQueryChange: (String) -> Unit,
    results: androidx.paging.compose.LazyPagingItems<EpisodeEntity>,
    onEpisodeClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 8.dp).background(NeoColors.ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTopBar(title = "Search", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NeoTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = "Search episodes",
                    modifier = Modifier.fillMaxWidth(),
                )
                AnimatedVisibility(visible = isEmptyQuery) {
                    Text(
                        text = "Enter a keyword to search",
                        fontSize = 12.sp,
                        color = NeoColors.TextSecondary,
                    )
                }
            }

            if (!isEmptyQuery) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
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
