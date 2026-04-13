package com.example.podcastapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.podcastapp.core.navigation.ExploreNavKey
import com.example.podcastapp.core.navigation.FavouritesNavKey
import com.example.podcastapp.core.navigation.HomeNavKey
import com.example.podcastapp.core.navigation.SearchNavKey
import com.example.podcastapp.core.ui.R

data class TopLevelNavItem(
    @param:StringRes val labelResId: Int,
    @param:DrawableRes val iconResId: Int,
    val id: String,
)

val TOP_LEVEL_NAV_ITEMS = linkedMapOf(
    HomeNavKey to TopLevelNavItem(R.string.nav_home, R.drawable.nav_home, "home"),
    ExploreNavKey to TopLevelNavItem(R.string.nav_explore, R.drawable.nav_explore, "explore"),
    FavouritesNavKey to TopLevelNavItem(R.string.nav_favourites, R.drawable.nav_like, "favourites"),
    SearchNavKey to TopLevelNavItem(R.string.nav_search, R.drawable.nav_setting, "search"),
)
