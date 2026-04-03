package com.example.podcastapp.core.data

import com.example.podcastapp.core.database.DownloadDao
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.EpisodeDao
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.database.PodcastDao
import com.example.podcastapp.core.database.PodcastEntity
import com.example.podcastapp.core.database.SubscriptionDao
import com.example.podcastapp.core.database.SubscriptionEntity
import com.example.podcastapp.core.network.RssFetcher
import kotlinx.coroutines.flow.Flow

class PodcastRepositoryImpl(
    private val podcastDao: PodcastDao,
    private val subscriptionDao: SubscriptionDao,
    private val rssFetcher: RssFetcher,
) : PodcastRepository {

    override fun observePodcasts(): Flow<List<PodcastEntity>> = podcastDao.observePodcasts()

    override fun observeSubscriptions(): Flow<List<SubscriptionEntity>> = subscriptionDao.observeSubscriptions()

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

    override suspend fun refreshEpisodes(podcastId: Long) {
        val podcast = podcastDao.getPodcast(podcastId) ?: return
        val feed = rssFetcher.fetch(podcast.feedUrl)
        val now = System.currentTimeMillis()

        val items = feed.items.map { item ->
            EpisodeEntity(
                podcastId = podcastId,
                guid = item.guid,
                title = item.title,
                description = item.description,
                audioUrl = item.audioUrl,
                imageUrl = item.imageUrl ?: podcast.imageUrl,
                durationSeconds = item.durationSeconds,
                pubDate = item.pubDate,
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

    override fun observeDownloads() = downloadDao.observeDownloads()

    override suspend fun getByEpisode(episodeId: Long) = downloadDao.getByEpisode(episodeId)

    override suspend fun getByDownloadManagerId(downloadManagerId: Long): DownloadEntity? =
        downloadDao.getByDownloadManagerId(downloadManagerId)

    override suspend fun upsert(download: DownloadEntity) {
        downloadDao.upsert(download)
    }
}
