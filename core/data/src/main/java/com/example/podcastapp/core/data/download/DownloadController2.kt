package com.example.podcastapp.core.data.download

import android.util.Printer
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.podcastapp.core.audioprocessing.WaveformGenerator
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.WaveformRepository
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DownloadController2 @Inject constructor(
    private val workManager: WorkManager,
    private val downloadRepository: DownloadRepository,
    private val waveformRepository: WaveformRepository,
    private val waveformGenerator: WaveformGenerator
) {
    suspend fun enqueue(episodeId: Long, title: String, url: String) {
        withContext(Dispatchers.IO) {
            // 先检查是否已经下载完成
            val existing = downloadRepository.getByEpisode(episodeId)
            if (existing != null && existing.status == DownloadStatus.COMPLETED && existing.localPath != null) {
                // 已经下载完成，不需要重复下载
                existing.localPath?.let {
                    generateWave(episodeId, it)

                }
                return@withContext
            }

            val downloadEntity = existing?.copy(status = DownloadStatus.QUEUED)
                ?: DownloadEntity(
                    episodeId = episodeId,
                    status = DownloadStatus.QUEUED,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

            downloadRepository.upsert(downloadEntity)

            // 2. 提交 WorkManager 任务
            val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(
                    downloadConstraint()
                )
                .setInputData(
                    workDataOf(
                        WORK_DATA_PARAM_EPISODE to episodeId,
                        WORK_DATA_PARAM_URL to url,
                        WORK_DATA_PARAM_TITLE to title
                    )
                )
                .addTag(getWorkTag(episodeId)) // 方便后续取消
                .build()

            workManager.enqueueUniqueWork(
                getWorkTag(episodeId),
                ExistingWorkPolicy.KEEP, // 如果已经在下载了，就保持原样
                downloadRequest
            )
        }
    }

    suspend fun cancel(episodeId: Long) {
        workManager.cancelUniqueWork(getWorkTag(episodeId))
        withContext(Dispatchers.IO) {
            // 同时更新数据库状态
            downloadRepository.updateDownloadStatus(episodeId, DownloadStatus.CANCELED, null)
        }
    }

    private fun downloadConstraint() =
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

    private fun getWorkTag(episodeId: Long) = "$WORK_TAG_PREFIX$episodeId"

    private suspend fun generateWave(id: Long, path: String) {
        val existing = waveformRepository.getWaveform(id)
        if (existing == null) {
            // 没有缓存才生成，避免重复工作
            val bars = waveformGenerator.generate(File(path).toUri())
            if (bars.isNotEmpty()) {
                waveformRepository.saveWaveform(id, bars)
            }
        }
    }

    companion object {
        const val WORK_TAG_PREFIX = "download_"
        const val WORK_DATA_PARAM_EPISODE = "episodeId"
        const val WORK_DATA_PARAM_URL = "url"
        const val WORK_DATA_PARAM_TITLE = "title"
    }
}