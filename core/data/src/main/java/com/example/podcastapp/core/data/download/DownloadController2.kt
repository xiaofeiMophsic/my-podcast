package com.example.podcastapp.core.data.download

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadController2(
    private val workManager: WorkManager,
    private val downloadRepository: DownloadRepository
) {
    suspend fun enqueue(episodeId: Long, title: String, url: String) {
        withContext(Dispatchers.IO) {
            // 1. 先往数据库占个位
            downloadRepository.upsert(
                DownloadEntity(
                    episodeId = episodeId,
                    status = DownloadStatus.QUEUED,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        // 2. 提交 WorkManager 任务
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .setInputData(
                workDataOf(
                    "episodeId" to episodeId,
                    "url" to url,
                    "title" to title
                )
            )
            .addTag("download_$episodeId") // 方便后续取消
            .build()

        workManager.enqueueUniqueWork(
            "download_$episodeId",
            ExistingWorkPolicy.KEEP, // 如果已经在下载了，就保持原样
            downloadRequest
        )
    }

    suspend fun cancel(episodeId: Long) {
        workManager.cancelUniqueWork("download_$episodeId")
        withContext(Dispatchers.IO) {
            // 同时更新数据库状态
            downloadRepository.updateDownloadStatus(episodeId, DownloadStatus.CANCELED, null)
        }
    }
}