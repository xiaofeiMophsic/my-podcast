package com.example.podcastapp.navigation

object NavRoutes {
    const val HOME = "home"
    const val NOW_PLAYING = "now_playing"
    const val PODCAST_LIST = "podcast_list"
    const val EPISODE_LIST = "episode_list"
    const val EPISODE_DETAIL = "episode_detail"
    const val SEARCH = "search"
    const val DOWNLOADS = "downloads"
    const val ADD_RSS = "add_rss"

    const val ARG_PODCAST_ID = "podcastId"
    const val ARG_EPISODE_ID = "episodeId"
    const val ARG_TARGET_EPISODE_ID = "targetEpisodeId"

    fun episodeListRoute(podcastId: Long) = "$EPISODE_LIST/$podcastId"
    fun episodeDetailRoute(episodeId: Long) = "$EPISODE_DETAIL/$episodeId"
    fun nowPlayingRoute(episodeId: Long? = null): String {
        return if (episodeId == null) NOW_PLAYING else "$NOW_PLAYING?$ARG_TARGET_EPISODE_ID=$episodeId"
    }
}
