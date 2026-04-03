package com.example.podcastapp.feature.episode

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


data class EpisodeUi(
    val id: Long,
    val title: String,
    val description: String?,
    val pubDate: String?,
)

fun formatDate(millis: Long?): String? {
    if (millis == null) return null
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
}
