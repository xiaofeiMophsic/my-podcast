package com.example.podcastapp.core.data

import com.example.podcastapp.core.database.DownloadDao
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.database.EpisodeWaveformEntity
import com.example.podcastapp.core.database.EpisodeDao
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.database.PodcastDao
import com.example.podcastapp.core.database.PodcastEntity
import com.example.podcastapp.core.database.SubscriptionDao
import com.example.podcastapp.core.database.SubscriptionEntity
import com.example.podcastapp.core.database.WaveformDao
import com.example.podcastapp.core.database.SearchHistoryDao
import com.example.podcastapp.core.database.SearchHistoryEntity
import com.example.podcastapp.core.network.RssFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PodcastRepositoryImpl(
    private val podcastDao: PodcastDao,
    private val subscriptionDao: SubscriptionDao,
    private val rssFetcher: RssFetcher,
) : PodcastRepository {

    override fun observePodcasts(): Flow<List<PodcastEntity>> = podcastDao.observePodcasts()

    override fun observeSubscriptions(): Flow<List<SubscriptionEntity>> = subscriptionDao.observeSubscriptions()

    override suspend fun getSubscribedPodcastIds(): List<Long> = subscriptionDao.getSubscribedPodcastIds()

    override suspend fun refreshPodcast(feedUrl: String): PodcastEntity {
        val now = System.currentTimeMillis()
        val feed = rssFetcher.fetch(feedUrl)
        val existing = podcastDao.getByFeedUrl(feedUrl)

        val entity = PodcastEntity(
            id = existing?.id ?: 0,
            feedUrl = feedUrl,
            title = feed.title,
            description = feed.description,
            imageUrl = feed.imageUrl,
            author = feed.author,
            language = feed.language,
            lastBuildDate = feed.lastBuildDate,
            updatedAt = now,
        )

        val id = podcastDao.upsert(entity)
        return if (entity.id != 0L) entity else entity.copy(id = id)
    }

    override suspend fun getPodcast(podcastId: Long): PodcastEntity? = podcastDao.getPodcast(podcastId)

    override suspend fun subscribe(podcastId: Long) {
        subscriptionDao.upsert(
            SubscriptionEntity(
                podcastId = podcastId,
                subscribedAt = System.currentTimeMillis(),
            )
        )
    }

    override suspend fun unsubscribe(podcastId: Long) {
        subscriptionDao.delete(podcastId)
    }
}

class EpisodeRepositoryImpl(
    private val podcastDao: PodcastDao,
    private val episodeDao: EpisodeDao,
    private val rssFetcher: RssFetcher,
) : EpisodeRepository {

    override fun pagingByPodcast(podcastId: Long) = episodeDao.pagingByPodcast(podcastId)

    override fun searchPaging(query: String) = episodeDao.searchPaging(query)

    override fun observeLatestEpisodes(podcastId: Long, limit: Int) =
        episodeDao.observeLatestByPodcast(podcastId, limit)

    override suspend fun refreshEpisodes(podcastId: Long) {
        val podcast = podcastDao.getPodcast(podcastId) ?: return
        val feed = rssFetcher.fetch(podcast.feedUrl)
        val now = System.currentTimeMillis()

        val existingIds = episodeDao.getEpisodeIdsByPodcast(podcastId).associate { it.guid to it.id }

        val items = feed.items.map { item ->
            EpisodeEntity(
                id = existingIds[item.guid] ?: 0,
                podcastId = podcastId,
                guid = item.guid,
                title = item.title,
                description = item.description,
                audioUrl = item.audioUrl,
                imageUrl = item.imageUrl ?: podcast.imageUrl,
                author = podcast.author,
                durationSeconds = item.durationSeconds,
                pubDate = item.pubDate,
                isPlayed = false,
                updatedAt = now,
            )
        }

        episodeDao.upsertAll(items)
    }

    override suspend fun getEpisode(episodeId: Long): EpisodeEntity? = episodeDao.getEpisode(episodeId)
}

class DownloadRepositoryImpl(
    private val downloadDao: DownloadDao,
) : DownloadRepository {

    override fun observeDownloads() = downloadDao.observeDownloads().distinctUntilChanged()

    override suspend fun getByEpisode(episodeId: Long) = downloadDao.getByEpisode(episodeId)

    override suspend fun getByDownloadManagerId(downloadManagerId: Long): DownloadEntity? =
        downloadDao.getByDownloadManagerId(downloadManagerId)

    override suspend fun upsert(download: DownloadEntity) {
        downloadDao.upsert(download)
    }

    // 供 Worker 在循环中调用
    override suspend fun updateDownloadProgress(id: Long, downloaded: Long, total: Long, status: DownloadStatus) {
        downloadDao.updateProgress(id, downloaded, total, status)
    }

    // 供 Worker 在任务结束时调用
    override suspend fun updateDownloadStatus(id: Long, status: DownloadStatus, path: String?) {
        downloadDao.updateStatus(id, status, path)
    }
}

class WaveformRepositoryImpl(
    private val waveformDao: WaveformDao,
) : WaveformRepository {

    override suspend fun getWaveform(episodeId: Long): List<Float>? {
        val entity = waveformDao.getByEpisodeId(episodeId) ?: return null
        if (entity.barsCsv.isBlank()) return null
        val bars = entity.barsCsv.split(",")
            .mapNotNull { it.toFloatOrNull() }
            .map { it.coerceIn(0f, 1f) }
        // 如果解析出来是空的，返回null让它重新生成
        return if (bars.isEmpty()) null else bars
    }

    override suspend fun saveWaveform(episodeId: Long, bars: List<Float>) {
        val csv = bars.joinToString(separator = ",") { it.coerceIn(0f, 1f).toString() }
        waveformDao.upsert(
            EpisodeWaveformEntity(
                episodeId = episodeId,
                barsCsv = csv,
                updatedAt = System.currentTimeMillis(),
            )
        )
    }
}

class SearchHistoryRepositoryImpl(
    private val searchHistoryDao: SearchHistoryDao,
) : SearchHistoryRepository {

    override fun observeHistory(limit: Int): Flow<List<String>> {
        return searchHistoryDao.observeRecent(limit).map { items -> items.map { it.query } }
    }

    override suspend fun addQuery(query: String, maxItems: Int) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val normalized = trimmed.lowercase()
        searchHistoryDao.upsert(
            SearchHistoryEntity(
                normalized = normalized,
                query = trimmed,
                updatedAt = System.currentTimeMillis(),
            )
        )
        searchHistoryDao.trimTo(maxItems)
    }

    override suspend fun clear() {
        searchHistoryDao.clear()
    }
}
