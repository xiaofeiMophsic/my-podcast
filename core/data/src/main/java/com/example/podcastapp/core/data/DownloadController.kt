package com.example.podcastapp.core.data

import android.app.DownloadManager
import android.content.Context
import androidx.core.net.toUri
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.media.WaveformGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadController(
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val downloadRepository: DownloadRepository,
    private val waveformRepository: WaveformRepository,
    private val waveformGenerator: WaveformGenerator,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val progressJobs = mutableMapOf<Long, kotlinx.coroutines.Job>()

    suspend fun enqueue(episodeId: Long, title: String, url: String) {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalFilesDir(
                context,
                null,
                "downloads/${sanitizeFileName(title)}.mp3",
            )

        val downloadId = downloadManager.enqueue(request)
        val now = System.currentTimeMillis()
        downloadRepository.upsert(
            DownloadEntity(
                episodeId = episodeId,
                downloadManagerId = downloadId,
                status = DownloadStatus.DOWNLOADING,
                progress = 0,
                createdAt = now,
                updatedAt = now,
            )
        )
        startProgressPolling(downloadId)
    }

    suspend fun handleDownloadComplete(downloadId: Long) {
        val download = downloadRepository.getByDownloadManagerId(downloadId) ?: return
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        cursor.use {
            if (!it.moveToFirst()) return
            val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            val localUri = it.getString(it.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
            val totalBytes = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloadedBytes = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

            val newStatus = when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                DownloadManager.STATUS_PAUSED -> DownloadStatus.QUEUED
                DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                DownloadManager.STATUS_PENDING -> DownloadStatus.QUEUED
                else -> DownloadStatus.FAILED
            }

            val updated = download.copy(
                localPath = localUri,
                status = newStatus,
                totalBytes = totalBytes,
                downloadedBytes = downloadedBytes,
                updatedAt = System.currentTimeMillis(),
            )
            downloadRepository.upsert(updated)

            if (newStatus == DownloadStatus.COMPLETED && !localUri.isNullOrBlank()) {
                withContext(Dispatchers.Default) {
                    val bars = waveformGenerator.generate(localUri.toUri())
                    if (bars.isNotEmpty()) {
                        waveformRepository.saveWaveform(download.episodeId, bars)
                    }
                }
            }
        }
    }

    private fun startProgressPolling(downloadId: Long) {
        if (progressJobs.containsKey(downloadId)) return
        val job = scope.launch {
            while (isActive) {
                val updated = queryProgress(downloadId) ?: break
                downloadRepository.upsert(updated)
                if (updated.status == DownloadStatus.COMPLETED ||
                    updated.status == DownloadStatus.FAILED ||
                    updated.status == DownloadStatus.CANCELED
                ) {
                    break
                }
                delay(DOWNLOAD_PROGRESS_QUERY_INTERVAL)
            }
            progressJobs.remove(downloadId)
        }
        progressJobs[downloadId] = job
    }

    private suspend fun queryProgress(downloadId: Long): DownloadEntity? {
        val current = downloadRepository.getByDownloadManagerId(downloadId) ?: return null
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        cursor.use {
            if (!it.moveToFirst()) return current
            val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            val localUri = it.getString(it.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
            val totalBytes = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloadedBytes = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

            val newStatus = when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                DownloadManager.STATUS_PAUSED -> DownloadStatus.QUEUED
                DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                DownloadManager.STATUS_PENDING -> DownloadStatus.QUEUED
                else -> DownloadStatus.FAILED
            }

            val progress = if (totalBytes > 0) {
                ((downloadedBytes * 100L) / totalBytes).toInt().coerceIn(0, 100)
            } else {
                current.progress
            }

            return current.copy(
                localPath = localUri ?: current.localPath,
                status = newStatus,
                totalBytes = if (totalBytes > 0) totalBytes else current.totalBytes,
                downloadedBytes = downloadedBytes,
                progress = progress,
                updatedAt = System.currentTimeMillis(),
            )
        }
    }

    private fun sanitizeFileName(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(80)
    }

    companion object {
        const val DOWNLOAD_PROGRESS_QUERY_INTERVAL = 800L
    }
}
