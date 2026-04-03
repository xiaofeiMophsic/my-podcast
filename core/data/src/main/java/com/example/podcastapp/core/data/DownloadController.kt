package com.example.podcastapp.core.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus

class DownloadController(
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val downloadRepository: DownloadRepository,
) {
    suspend fun enqueue(episodeId: Long, title: String, url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
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
        }
    }

    private fun sanitizeFileName(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(80)
    }
}
