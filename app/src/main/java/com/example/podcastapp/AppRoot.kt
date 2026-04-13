package com.example.podcastapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.podcastapp.core.navigation.HomeNavKey
import com.example.podcastapp.core.navigation.Navigator
import com.example.podcastapp.core.navigation.navigateToNowPlaying
import com.example.podcastapp.core.navigation.rememberNavigationState
import com.example.podcastapp.core.navigation.toEntries
import com.example.podcastapp.core.player.GlobalMiniPlayerBar
import com.example.podcastapp.core.ui.neo.NeoBottomNav
import com.example.podcastapp.core.ui.neo.NeoColors
import com.example.podcastapp.core.ui.neo.NeoNavItem
import com.example.podcastapp.core.ui.neo.LocalSnackbarHostState
import com.example.podcastapp.navigation.TOP_LEVEL_NAV_ITEMS
import com.example.podcastapp.navigation.addRssEntry
import com.example.podcastapp.navigation.downloadEntry
import com.example.podcastapp.navigation.episodeDetailEntry
import com.example.podcastapp.navigation.episodeListEntry
import com.example.podcastapp.navigation.exploreEntry
import com.example.podcastapp.navigation.favouritesEntry
import com.example.podcastapp.navigation.homeEntry
import com.example.podcastapp.navigation.nowPlayingEntry
import com.example.podcastapp.navigation.searchEntry

@Composable
fun AppRoot() {
    val navigationState = rememberNavigationState(
        startKey = HomeNavKey,
        topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys,
    )
    val navigator = remember { Navigator(navigationState) }

    val isTopLevel by remember {
        derivedStateOf { navigationState.currentKey in navigationState.topLevelKeys }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Build NeoNavItem list from TOP_LEVEL_NAV_ITEMS (needs @Composable for stringResource)
    val navItems = TOP_LEVEL_NAV_ITEMS.map { (_, item) ->
        NeoNavItem(
            label = stringResource(item.labelResId),
            iconResId = item.iconResId,
            id = item.id,
        )
    }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            bottomBar = {
                if (isTopLevel) {
                    Column {
                        GlobalMiniPlayerBar(
                            onPlayListClick = { /* TODO */ },
                            onPlayerClick = { episodeId -> navigator.navigateToNowPlaying(episodeId) },
                        )
                        NeoBottomNav(
                            items = navItems,
                            selectedItemId = TOP_LEVEL_NAV_ITEMS[navigationState.currentTopLevelKey]?.id
                                ?: "home",
                            onItemClick = { item ->
                                val navKey = TOP_LEVEL_NAV_ITEMS.entries
                                    .firstOrNull { it.value.id == item.id }?.key
                                if (navKey != null) navigator.navigate(navKey)
                            },
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = NeoColors.ScreenBg,
        ) { padding ->
            val entryProvider = entryProvider {
                homeEntry(navigator)
                exploreEntry(navigator)
                favouritesEntry(navigator)
                searchEntry(navigator)
                nowPlayingEntry(navigator)
                episodeListEntry(navigator)
                episodeDetailEntry(navigator)
                downloadEntry(navigator)
                addRssEntry(navigator)
            }

            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
            )
        }
    }
}
