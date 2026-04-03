package com.example.podcastapp.navigation

object NavRoutes {
    const val HOME = "home"
    const val NOW_PLAYING = "now_playing"
    const val PODCAST_LIST = "podcast_list"
    const val EPISODE_LIST = "episode_list"
    const val EPISODE_DETAIL = "episode_detail"
    const val SEARCH = "search"
    const val DOWNLOADS = "downloads"

    const val ARG_PODCAST_ID = "podcastId"
    const val ARG_EPISODE_ID = "episodeId"

    fun episodeListRoute(podcastId: Long) = "$EPISODE_LIST/$podcastId"
    fun episodeDetailRoute(episodeId: Long) = "$EPISODE_DETAIL/$episodeId"
}
