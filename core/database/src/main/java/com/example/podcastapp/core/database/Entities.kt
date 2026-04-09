package com.example.podcastapp.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "podcasts",
    indices = [Index(value = ["feedUrl"], unique = true)],
)
data class PodcastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val feedUrl: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val author: String?,
    val language: String?,
    val lastBuildDate: Long?,
    val updatedAt: Long,
)

@Entity(
    tableName = "episodes",
    indices = [
        Index(value = ["podcastId"]),
        Index(value = ["guid"], unique = true),
        Index(value = ["pubDate"]),
    ],
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val podcastId: Long,
    val guid: String,
    val title: String,
    val description: String?,
    val audioUrl: String,
    val imageUrl: String?,
    val author: String?,
    val durationSeconds: Long?,
    val pubDate: Long?,
    val isPlayed: Boolean = false,
    val updatedAt: Long,
)

@Entity(
    tableName = "subscriptions",
    indices = [Index(value = ["podcastId"], unique = true)],
)
data class SubscriptionEntity(
    @PrimaryKey val podcastId: Long,
    val subscribedAt: Long,
)

@Entity(
    tableName = "downloads",
    indices = [Index(value = ["episodeId"], unique = true)],
)
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val episodeId: Long,
    val downloadManagerId: Long? = null,
    val localPath: String? = null,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Int = 0,
    val totalBytes: Long = 0,
    val downloadedBytes: Long = 0,
    val createdAt: Long,
    val updatedAt: Long,
)

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELED,
}

data class EpisodeIdAndGuid(
    val id: Long,
    val guid: String,
)

@Entity(
    tableName = "search_history",
    indices = [Index(value = ["updatedAt"])],
)
data class SearchHistoryEntity(
    @PrimaryKey val normalized: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val query: String,
    val updatedAt: Long,
)
