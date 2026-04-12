package com.example.podcastapp.feature.podcast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.player.GlobalMiniPlayerBar
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.ShadowCard

// ─── Asset URLs (Figma node 1:183, valid 7 days) ──────────────────────────────
// Header
private const val IMG_DOTS_DECO   = "https://www.figma.com/api/mcp/asset/e83940e1-54f9-44d7-b444-673dba71ca84"
private const val IMG_AVATAR_BG   = "https://www.figma.com/api/mcp/asset/3177c65c-83cf-4f02-be8a-3e0e72e7a7b3"
private const val IMG_AVATAR_BODY = "https://www.figma.com/api/mcp/asset/082f0573-5b75-4b8a-95a8-0c777b931270"
private const val IMG_AVATAR_HEAD = "https://www.figma.com/api/mcp/asset/40bd4884-4869-4972-b029-74ed44b24c5c"
private const val IMG_AVATAR_FACE = "https://www.figma.com/api/mcp/asset/e313aa00-e61c-4bd5-93d1-1905c83b8005"
private const val IMG_AVATAR_ACC  = "https://www.figma.com/api/mcp/asset/8c8198f0-f64e-4dfb-bd4d-c4680c56763f"
private const val IMG_WAVY_1      = "https://www.figma.com/api/mcp/asset/38685650-221e-4121-aff2-f08b12ce03e4"
private const val IMG_WAVY_2      = "https://www.figma.com/api/mcp/asset/7c5cd918-9ff8-40f3-aad0-98f23cc778ff"
private const val IMG_SPARKLE     = "https://www.figma.com/api/mcp/asset/10dc0eeb-4484-4763-b4c1-6cd441efb609"
// Content
private const val SEARCH_ICON     = "https://www.figma.com/api/mcp/asset/d45bdcf0-5387-4b80-ab70-5a0d84e58e2b"
private const val PLAY_CIRCLE     = "https://www.figma.com/api/mcp/asset/ac6bbaf8-b776-443d-b178-1d023555cb72"
// Bottom nav
private const val NAV_HOME        = "https://www.figma.com/api/mcp/asset/765adfbd-db62-4684-b61d-07607ceb8871"
private const val NAV_EXPLORE     = "https://www.figma.com/api/mcp/asset/77750278-6c21-4f48-9076-7ff65459d689"
private const val NAV_HEART       = "https://www.figma.com/api/mcp/asset/81ec20c9-76a4-4382-8ab6-6631313e5da6"
private const val NAV_SEARCH      = "https://www.figma.com/api/mcp/asset/8db57dd9-f46d-49e0-81d4-942588b84f9f"

@Composable
fun HomeRoute(
    onSearchClick: () -> Unit = {},
    onPlayerClick: (episodeId: Long) -> Unit = {},
    onAddRssClick: () -> Unit = {},
    onPlayListClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onSearchClick = onSearchClick,
        onAddRssClick = onAddRssClick,
        onEpisodeClick = { episode ->
//            viewModel.playEpisode(episode)
            onPlayerClick(episode.id)
        },
        onPlayListClick = onPlayListClick,
        onPlayerClick = onPlayerClick,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onSearchClick: () -> Unit = {},
    onAddRssClick: () -> Unit = {},
    onEpisodeClick: (EpisodeEntity) -> Unit = {},
    onPlayListClick: () -> Unit = {},
    onPlayerClick: (episodeId: Long) -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .navigationBarsPadding()
            ) {
                GlobalMiniPlayerBar(
                    onPlayListClick = onPlayListClick,
                    onPlayerClick = onPlayerClick,
                )
                HomeBottomNav(onSearchClick = onSearchClick, onAddRssClick = onAddRssClick)
            }
        },
        containerColor = NeoColors.ScreenBg,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState()),
            ) {
                HomeHeader(Modifier.statusBarsPadding())

                Column(
                    modifier = Modifier.padding(vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    HomeSearchBar(
                        modifier = Modifier
                            .padding(horizontal = 25.dp)
                            .fillMaxWidth(),
                        onClick = onSearchClick,
                    )

                    if (state.sections.isEmpty()) {
                        EmptySubscriptionsHint(onClick = onAddRssClick)
                    } else {
                        state.sections.forEach { section ->
                            PodcastSectionBlock(
                                section = section,
                                onEpisodeClick = onEpisodeClick,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            AsyncImage(
                model = IMG_DOTS_DECO,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 61.dp, height = 53.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun EmptySubscriptionsHint(onClick: () -> Unit) {
    ShadowCard(
        modifier = Modifier
            .padding(horizontal = 25.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "No subscriptions yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeoColors.TextPrimary,
            )
            Text(
                text = "Add a RSS feed to see podcasts here.",
                fontSize = 13.sp,
                color = NeoColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun PodcastSectionBlock(
    section: PodcastSection,
    onEpisodeClick: (EpisodeEntity) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = section.podcast.title,
            modifier = Modifier.padding(horizontal = 25.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NeoColors.TextPrimary,
        )

        val episodes by section.episodesFlow.collectAsState(initial = emptyList())
        LazyRow(
            contentPadding = PaddingValues(horizontal = 25.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(episodes) { episode ->
                EpisodeCard(episode = episode, onEpisodeClick = onEpisodeClick)
            }
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: EpisodeEntity,
    onEpisodeClick: (EpisodeEntity) -> Unit,
) {
    ShadowCard(
        modifier = Modifier
            .size(width = 105.dp, height = 148.dp)
            .clickable { onEpisodeClick(episode) }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 4.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(69.dp)
                    .border(2.dp, NeoColors.AccentGreen, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = episode.imageUrl,
                    contentDescription = episode.description,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = episode.title,
                color = NeoColors.TextPrimary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────
@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box {
                Text(
                    text = "Good Morning!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoColors.TextPrimary,
                    letterSpacing = (-0.56).sp,
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(y = 26.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    AsyncImage(
                        model = IMG_WAVY_1,
                        contentDescription = null,
                        modifier = Modifier.size(width = 107.dp, height = 8.dp),
                    )
                    AsyncImage(
                        model = IMG_WAVY_2,
                        contentDescription = null,
                        modifier = Modifier.size(width = 93.dp, height = 7.dp),
                    )
                }
            }

            Text(
                text = "Ready to listen?",
                fontSize = 14.sp,
                color = NeoColors.TextSecondary,
                letterSpacing = (-0.28).sp,
            )
        }

        Box(
            modifier = Modifier
                .size(86.dp)
                .align(Alignment.CenterEnd),
        ) {
            AsyncImage(model = IMG_AVATAR_BG, contentDescription = null, modifier = Modifier.fillMaxSize())
            AsyncImage(model = IMG_AVATAR_BODY, contentDescription = null, modifier = Modifier.fillMaxSize())
            AsyncImage(model = IMG_AVATAR_HEAD, contentDescription = null, modifier = Modifier.fillMaxSize())
            AsyncImage(model = IMG_AVATAR_FACE, contentDescription = null, modifier = Modifier.fillMaxSize())
            AsyncImage(model = IMG_AVATAR_ACC, contentDescription = null, modifier = Modifier.fillMaxSize())
            AsyncImage(
                model = IMG_SPARKLE,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 6.dp, y = 2.dp),
            )
        }
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────
@Composable
private fun HomeSearchBar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ShadowCard(modifier = modifier.clickable{ onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(model = SEARCH_ICON, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search",
                fontSize = 14.sp,
                color = NeoColors.TextSecondary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Browse",
                fontSize = 12.sp,
                color = NeoColors.TextSecondary,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}

// ─── Bottom Navigation ────────────────────────────────────────────────────────
private data class NavItem(val label: String, val iconUrl: String, val isActive: Boolean = false)

private val navItems = listOf(
    NavItem("Home",       NAV_HOME,    isActive = true),
    NavItem("Explore",    NAV_EXPLORE),
    NavItem("Favourites", NAV_HEART),
    NavItem("Search",     NAV_SEARCH),
)

@Composable
private fun HomeBottomNav(
    onSearchClick: () -> Unit = {},
    onAddRssClick: () -> Unit = {},
) {
    ShadowCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.forEachIndexed { index, item ->
                NavTab(
                    item = item,
                    onClick = {
                        when (index) {
                            1 -> onAddRssClick()
                            3 -> onSearchClick()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun NavTab(item: NavItem, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { onClick() },
    ) {
        AsyncImage(
            model = item.iconUrl,
            contentDescription = item.label,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (item.isActive) NeoColors.TextPrimary else NeoColors.TextSecondary,
        )
    }
}
