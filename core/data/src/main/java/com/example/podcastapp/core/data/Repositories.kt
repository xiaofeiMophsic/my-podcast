package com.example.podcastapp.core.data

import androidx.paging.PagingSource
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.database.PodcastEntity
import com.example.podcastapp.core.database.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    fun observePodcasts(): Flow<List<PodcastEntity>>
    fun observeSubscriptions(): Flow<List<SubscriptionEntity>>
    suspend fun getSubscribedPodcastIds(): List<Long>
    suspend fun refreshPodcast(feedUrl: String): PodcastEntity
    suspend fun getPodcast(podcastId: Long): PodcastEntity?
    suspend fun subscribe(podcastId: Long)
    suspend fun unsubscribe(podcastId: Long)
}

interface EpisodeRepository {
    fun pagingByPodcast(podcastId: Long): PagingSource<Int, EpisodeEntity>
    fun searchPaging(query: String): PagingSource<Int, EpisodeEntity>
    suspend fun refreshEpisodes(podcastId: Long)
    suspend fun getEpisode(episodeId: Long): EpisodeEntity?
    fun observeLatestEpisodes(podcastId: Long, limit: Int): Flow<List<EpisodeEntity>>
}

interface DownloadRepository {
    fun observeDownloads(): Flow<List<DownloadEntity>>
    suspend fun getByEpisode(episodeId: Long): DownloadEntity?
    suspend fun getByDownloadManagerId(downloadManagerId: Long): DownloadEntity?
    suspend fun upsert(download: DownloadEntity)

    // 供 Worker 在循环中调用
    suspend fun updateDownloadProgress(id: Long, downloaded: Long, total: Long, status: DownloadStatus)

    // 供 Worker 在任务结束时调用
    suspend fun updateDownloadStatus(id: Long, status: DownloadStatus, path: String?)
}

interface WaveformRepository {
    suspend fun getWaveform(episodeId: Long): List<Float>?
    suspend fun saveWaveform(episodeId: Long, bars: List<Float>)
}

interface SearchHistoryRepository {
    fun observeHistory(limit: Int = 8): Flow<List<String>>
    suspend fun addQuery(query: String, maxItems: Int = 8)
    suspend fun clear()
}
