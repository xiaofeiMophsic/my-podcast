package com.example.podcastapp.core.data.download

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.WaveformRepository
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.audioprocessing.WaveformGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: DownloadRepository,
    private val waveformGenerator: WaveformGenerator,
    private val waveformRepository: WaveformRepository,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val episodeId = inputData.getLong(DownloadController2.WORK_DATA_PARAM_EPISODE, -1L)
        val url = inputData.getString(DownloadController2.WORK_DATA_PARAM_URL) ?: return Result.failure()
        val title = inputData.getString(DownloadController2.WORK_DATA_PARAM_TITLE) ?: "unknown"

        if (episodeId == -1L) return Result.failure()

        return try {
            val request = Request.Builder()
                .url(url)
//                .header("User-Agent", "YourPodcastApp/1.0") // 可自定义 Header
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body ?: throw IOException("Empty body")
            val totalBytes = body.contentLength()
            val file = File(context.getExternalFilesDir(null), "downloads/${episodeId}.mp3")

            // 写入文件并实时更新进度
            body.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    var downloadedBytes = 0L
                    var lastUpdateTime = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        // 性能优化：每 500ms 更新一次数据库，避免过于频繁
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastUpdateTime > 500L) {
                            updateProgress(episodeId, downloadedBytes, totalBytes)
                            lastUpdateTime = currentTime
                        }
                    }
                }
            }

            // 下载完成后的后续处理
            handleSuccess(episodeId, file.absolutePath)
            Result.success()
        } catch (e: Exception) {
            handleFailure(episodeId)
            Result.failure()
        }
    }

    private suspend fun updateProgress(id: Long, downloaded: Long, total: Long) {
        repository.updateDownloadProgress(id, downloaded, total, DownloadStatus.DOWNLOADING)
        // WorkManager 内部进度，可供监听
        setProgress(workDataOf("progress" to (downloaded * 100 / total).toInt()))
    }

    private suspend fun handleSuccess(id: Long, path: String) {
        repository.updateDownloadStatus(id, DownloadStatus.COMPLETED, path)
        // 检查是否已经有波形缓存了（播放可能已经生成过）
        val existing = waveformRepository.getWaveform(id)
        if (existing == null) {
            // 没有缓存才生成，避免重复工作
            val bars = waveformGenerator.generate(File(path).toUri())
            if (bars.isNotEmpty()) {
                waveformRepository.saveWaveform(id, bars)
            }
        }
    }

    private suspend fun handleFailure(id: Long) {
        repository.updateDownloadStatus(id, DownloadStatus.FAILED, null)
    }
}