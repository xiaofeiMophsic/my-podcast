package com.example.podcastapp.core.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {
    @Query("SELECT * FROM podcasts ORDER BY title")
    fun observePodcasts(): Flow<List<PodcastEntity>>

    @Upsert
    suspend fun upsert(item: PodcastEntity): Long

    @Upsert
    suspend fun upsertAll(items: List<PodcastEntity>): List<Long>

    @Query("SELECT * FROM podcasts WHERE id = :podcastId")
    suspend fun getPodcast(podcastId: Long): PodcastEntity?

    @Query("SELECT * FROM podcasts WHERE feedUrl = :feedUrl")
    suspend fun getByFeedUrl(feedUrl: String): PodcastEntity?
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE podcastId = :podcastId ORDER BY pubDate DESC")
    fun pagingByPodcast(podcastId: Long): PagingSource<Int, EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    suspend fun getEpisode(episodeId: Long): EpisodeEntity?

    @Query("SELECT * FROM episodes WHERE title LIKE '%' || :query || '%' ORDER BY pubDate DESC")
    fun searchPaging(query: String): PagingSource<Int, EpisodeEntity>

    @Query("SELECT id, guid FROM episodes WHERE podcastId = :podcastId")
    suspend fun getEpisodeIdsByPodcast(podcastId: Long): List<EpisodeIdAndGuid>

    @Upsert
    suspend fun upsertAll(items: List<EpisodeEntity>)

    @Update
    suspend fun update(item: EpisodeEntity)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions")
    fun observeSubscriptions(): Flow<List<SubscriptionEntity>>

    @Upsert
    suspend fun upsert(item: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE podcastId = :podcastId")
    suspend fun delete(podcastId: Long)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads WHERE episodeId = :episodeId")
    suspend fun getByEpisode(episodeId: Long): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE downloadManagerId = :downloadManagerId")
    suspend fun getByDownloadManagerId(downloadManagerId: Long): DownloadEntity?

    @Query("SELECT * FROM downloads ORDER BY updatedAt DESC")
    fun observeDownloads(): Flow<List<DownloadEntity>>

    @Upsert
    suspend fun upsert(item: DownloadEntity)

    @Update
    suspend fun update(item: DownloadEntity)
}
