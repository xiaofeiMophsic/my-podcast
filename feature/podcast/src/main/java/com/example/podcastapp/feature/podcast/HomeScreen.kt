package com.example.podcastapp.feature.podcast

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ScreenBg      = Color(0xFFFFFFED)
private val CardBg        = Color(0xFFFFFFFF)
private val CardBorder    = Color(0xFF000000)
private val ShadowColor   = Color(0x40000000)
private val TextPrimary   = Color(0xFF000000)
private val TextSecondary = Color(0xFF6B6B6B)
private val AccentGreen   = Color(0xFF87B800)
private val NavBg         = Color(0xFFFFFFFF)
private val NavBorder     = Color(0xFFD9D9D9)

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
private const val STORY_RING      = "https://www.figma.com/api/mcp/asset/13c8710a-26de-41a2-a960-8c05232ff84b"
private const val PLAY_CIRCLE     = "https://www.figma.com/api/mcp/asset/ac6bbaf8-b776-443d-b178-1d023555cb72"
// Bottom nav
private const val NAV_HOME        = "https://www.figma.com/api/mcp/asset/765adfbd-db62-4684-b61d-07607ceb8871"
private const val NAV_EXPLORE     = "https://www.figma.com/api/mcp/asset/77750278-6c21-4f48-9076-7ff65459d689"
private const val NAV_HEART       = "https://www.figma.com/api/mcp/asset/81ec20c9-76a4-4382-8ab6-6631313e5da6"
private const val NAV_SEARCH      = "https://www.figma.com/api/mcp/asset/8db57dd9-f46d-49e0-81d4-942588b84f9f"

// ─── Data ─────────────────────────────────────────────────────────────────────
private data class Story(val name: String, val avatarUrl: String)

private val stories = listOf(
    Story("twenty one pilots", "https://www.figma.com/api/mcp/asset/2b3e0808-619f-4923-b27b-3c75db4d30a0"),
    Story("A.R Rahman",        "https://www.figma.com/api/mcp/asset/9c5660c8-64d3-43ab-a704-aaebcec61deb"),
    Story("Linkin Park",       "https://www.figma.com/api/mcp/asset/4f1635e3-3488-4e05-bd30-4556020b9ad2"),
    Story("twenty one pilots", "https://www.figma.com/api/mcp/asset/73f15b2a-5226-46b3-86ee-1681c23f55ac"),
)

private data class Album(val title: String, val coverUrl: String)

private val recentlyPlayed = listOf(
    Album("Dawn FM",                "https://www.figma.com/api/mcp/asset/781baf8a-9f3a-42dd-81db-a71c8bd3be3d"),
    Album("Trench",                 "https://www.figma.com/api/mcp/asset/4b9ad2e5-cd57-448d-8ea5-14955ab14990"),
    Album("Gangs of Wasseypur - 2", "https://www.figma.com/api/mcp/asset/fda7e16d-391e-4b1a-bede-3cf3acb5ad2d"),
    Album("Dawn FM",                "https://www.figma.com/api/mcp/asset/781baf8a-9f3a-42dd-81db-a71c8bd3be3d"),
)

private data class MoodCard(val imageUrl: String)

private val moodsAndGenres = listOf(
    MoodCard("https://www.figma.com/api/mcp/asset/7bd29f65-8b04-4380-9009-ecabbd0d398d"),
    MoodCard("https://www.figma.com/api/mcp/asset/854c8494-b834-4109-947c-2795730c2019"),
    MoodCard("https://www.figma.com/api/mcp/asset/d53c907d-449d-4a28-a27a-3e2f4d8d63b1"),
)

// ─── Neo-brutalist shadow card ─────────────────────────────────────────────────
@Composable
private fun ShadowCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(shape)
                .background(ShadowColor),
        )
        Surface(
            shape = shape,
            color = CardBg,
            border = BorderStroke(0.5.dp, CardBorder),
            content = content,
        )
    }
}

// ─── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    onPlayerClick: () -> Unit = {},
) {
    Scaffold(
        bottomBar = { HomeBottomNav(onSearchClick = onSearchClick) },
        containerColor = ScreenBg,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Header
                HomeHeader()

                // Content sections
                Column(
                    modifier = Modifier.padding(vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    HomeSearchBar(
                        modifier = Modifier
                            .padding(horizontal = 25.dp)
                            .fillMaxWidth(),
                        onClick = onSearchClick,
                    )
                    StoriesSection()
                    RecentlyPlayedSection(onItemClick = onPlayerClick)
                    MoodsAndGenresSection()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Decorative dots — top-right of screen
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

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .padding(top = 54.dp, bottom = 16.dp),
    ) {
        // Greeting + wavy underlines
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box {
                Text(
                    text = "Good Morning!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.56).sp,
                )
                // Wavy underlines (decorative, drawn just below title)
                AsyncImage(
                    model = IMG_WAVY_1,
                    contentDescription = null,
                    modifier = Modifier
                        .width(186.dp)
                        .height(4.dp)
                        .align(Alignment.BottomStart)
                        .offset(y = 6.dp),
                )
                AsyncImage(
                    model = IMG_WAVY_2,
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(3.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 17.dp, y = 9.dp),
                )
            }
            Text(
                text = "Let's play some music!",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
        }

        // User avatar (circle + layered character)
        UserAvatar(modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun UserAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(49.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Green circle background
        AsyncImage(
            model = IMG_AVATAR_BG,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        // Layered character (body → head → face → accessories)
        AsyncImage(
            model = IMG_AVATAR_BODY,
            contentDescription = null,
            modifier = Modifier
                .size(width = 41.dp, height = 39.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds,
        )
        AsyncImage(
            model = IMG_AVATAR_HEAD,
            contentDescription = null,
            modifier = Modifier
                .size(width = 28.dp, height = 31.dp)
                .align(Alignment.TopCenter)
                .offset(y = 6.dp),
            contentScale = ContentScale.FillBounds,
        )
        AsyncImage(
            model = IMG_AVATAR_ACC,
            contentDescription = null,
            modifier = Modifier
                .size(width = 21.dp, height = 6.dp)
                .align(Alignment.TopCenter)
                .offset(y = 23.dp),
        )
    }
}

// ─── Search bar ───────────────────────────────────────────────────────────────
@Composable
private fun HomeSearchBar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ShadowCard(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(15.dp),
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 21.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                AsyncImage(
                    model = SEARCH_ICON,
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = "Search for music or podcast",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ─── Stories ──────────────────────────────────────────────────────────────────
@Composable
private fun StoriesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "Stories",
            actionLabel = "View All",
            modifier = Modifier.padding(horizontal = 25.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 25.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(stories) { story -> StoryCard(story) }
        }
    }
}

@Composable
private fun StoryCard(story: Story) {
    ShadowCard(
        modifier = Modifier.size(width = 101.dp, height = 134.dp),
        shape = RoundedCornerShape(15.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.size(69.dp)) {
                AsyncImage(
                    model = story.avatarUrl,
                    contentDescription = story.name,
                    modifier = Modifier
                        .size(59.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                AsyncImage(
                    model = STORY_RING,
                    contentDescription = null,
                    modifier = Modifier
                        .size(69.dp)
                        .align(Alignment.Center),
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = story.name,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.width(60.dp),
            )
        }
    }
}

// ─── Recently Played ──────────────────────────────────────────────────────────
@Composable
private fun RecentlyPlayedSection(onItemClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Header with sparkle icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Recently played",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                AsyncImage(
                    model = IMG_SPARKLE,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = "View More",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textDecoration = TextDecoration.Underline,
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recentlyPlayed) { album -> AlbumCard(album, onClick = onItemClick) }
        }
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShadowCard(
            modifier = Modifier
                .size(113.dp)
                .clickable(onClick = onClick),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = album.coverUrl,
                    contentDescription = album.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                AsyncImage(
                    model = PLAY_CIRCLE,
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp),
                )
            }
        }
        Text(
            text = album.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.width(85.dp),
        )
    }
}

// ─── Moods & Genres ───────────────────────────────────────────────────────────
@Composable
private fun MoodsAndGenresSection() {
    Column(
        modifier = Modifier.padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionHeader(title = "Moods & Genres", actionLabel = "View More")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(moodsAndGenres) { mood -> MoodGenreCard(mood) }
        }
    }
}

@Composable
private fun MoodGenreCard(mood: MoodCard) {
    ShadowCard(modifier = Modifier.size(113.dp)) {
        AsyncImage(
            model = mood.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
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
private fun HomeBottomNav(onSearchClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NavBg,
        border = BorderStroke(1.dp, NavBorder),
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.forEach { item ->
                NavTab(
                    item = item,
                    onClick = if (item.label == "Search") onSearchClick else ({}),
                )
            }
        }
    }
}

@Composable
private fun NavTab(item: NavItem, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .width(63.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = if (item.isActive) {
                Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(AccentGreen)
            } else {
                Modifier.size(45.dp)
            },
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = item.iconUrl,
                contentDescription = item.label,
                modifier = if (item.isActive) Modifier.size(28.dp) else Modifier.size(45.dp),
            )
        }
        Text(
            text = item.label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

// ─── Shared section header ────────────────────────────────────────────────────
@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = actionLabel,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            textDecoration = TextDecoration.Underline,
        )
    }
}
