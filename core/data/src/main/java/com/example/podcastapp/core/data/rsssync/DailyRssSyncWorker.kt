package com.example.podcastapp.core.data.rsssync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.data.PodcastRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class DailyRssSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val podcastRepository: PodcastRepository,
    private val episodeRepository: EpisodeRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val subscribedIds = podcastRepository.getSubscribedPodcastIds()
            subscribedIds.forEach { podcastId ->
                episodeRepository.refreshEpisodes(podcastId)
            }
            Result.success()
        } catch (_: IOException) {
            Result.retry()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "daily_rss_sync_work"
    }
}
