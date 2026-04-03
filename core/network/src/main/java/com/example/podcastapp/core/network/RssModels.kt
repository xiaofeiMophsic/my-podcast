package com.example.podcastapp.core.network

data class RssFeed(
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val author: String?,
    val language: String?,
    val lastBuildDate: Long?,
    val items: List<RssItem>,
)

data class RssItem(
    val guid: String,
    val title: String,
    val description: String?,
    val audioUrl: String,
    val imageUrl: String?,
    val durationSeconds: Long?,
    val pubDate: Long?,
)
