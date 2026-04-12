package com.example.podcastapp.core.audioprocessing

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.linc.amplituda.Amplituda
import com.linc.amplituda.AmplitudaProgressListener
import com.linc.amplituda.Cache
import com.linc.amplituda.Compress
import com.linc.amplituda.ProgressOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.measureTimedValue

class WaveformGenerator(
    private val context: Context,
) {
    private val amplituda: Amplituda by lazy { Amplituda(context) }

    suspend fun generate(uri: Uri, maxBars: Int = DEFAULT_MAX_BARS): FloatArray = withContext(Dispatchers.IO) {
        val (source, tempFile) = resolveSource(uri) ?: return@withContext floatArrayOf()
        try {
            val durationMs = extractDurationMs(uri, source)
            val targetBars = if (maxBars > 0) maxBars else computeTargetBars(durationMs)
            val amplitudes = process(source)
            if (amplitudes.isEmpty()) return@withContext floatArrayOf()

            val sampleData =
                measureTimedValue { downsampleAndNormalize(amplitudes, targetBars) }
            Timber.d("Downsampled in ${sampleData.duration}")
            sampleData.value
        } catch (e: Throwable) {
            floatArrayOf()
        } finally {
            tempFile?.delete()
        }
    }

    private suspend fun process(source: String): List<Int> {
        val debugListener = object : AmplitudaProgressListener() {
            override fun onProgress(p0: ProgressOperation?, p1: Int) {}

            override fun onStartProgress() {
                Timber.d("onStart generate wave")
            }

            override fun onStopProgress() {
                Timber.d("onStop generate wave")
            }

        }
        val output = amplituda.processAudio(source, Compress.withParams(1, 1), Cache.withParams(Cache.REUSE), debugListener)
        return suspendCancellableCoroutine { cont ->
            output.get(
                { result -> cont.resume(result.amplitudesAsList()); },
                { error -> cont.resumeWithException(error) }
            )
        }
    }

    private fun resolveSource(uri: Uri): Pair<String, File?>? {
        return when (uri.scheme?.lowercase()) {
            "http", "https" -> {
                // Amplituda's internal network handling is buggy and causes ArrayIndexOutOfBoundsException
                // Remote URLs must be downloaded locally first before processing
                null
            }
            "content" -> copyToCache(uri)
            "file" -> uri.path?.let { it to null }
            null -> null
            else -> null
        }
    }

    private fun copyToCache(uri: Uri): Pair<String, File?>? {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("waveform_", ".audio", context.cacheDir)
        input.use { src ->
            tempFile.outputStream().use { dst ->
                src.copyTo(dst)
            }
        }
        return tempFile.absolutePath to tempFile
    }

    private fun extractDurationMs(uri: Uri, source: String): Long? {
        val retriever = MediaMetadataRetriever()
        return try {
            when (uri.scheme?.lowercase()) {
                "content" -> retriever.setDataSource(context, uri)
                "file" -> retriever.setDataSource(uri.path)
                "http", "https" -> retriever.setDataSource(source, emptyMap())
                else -> retriever.setDataSource(source)
            }
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration?.toLongOrNull()
        } catch (_: Throwable) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun computeTargetBars(durationMs: Long?): Int {
        if (durationMs == null || durationMs <= 0L) return FALLBACK_BARS
        val seconds = durationMs / 1000f
        val bars = (seconds * BARS_PER_SECOND).toInt()
        return bars.coerceIn(MIN_BARS, MAX_BARS)
    }

    fun downsampleAndNormalize(values: List<Int>, targetBars: Int): FloatArray {
        if (values.isEmpty() || targetBars <= 0) return FloatArray(0)

        val actualBars = min(values.size, targetBars)
        val result = FloatArray(actualBars)

        // 降采样产生的中间结果很少（比如4000个），这里自己用基本类型数组可以保证速度
        val rawDownsampled = IntArray(actualBars)
        val bucketSize = values.size.toDouble() / actualBars

        var globalSumSq = 0L // 使用 Long 避免 Int 溢出
        var globalMaxInt = 0

        // ==========================================
        // 第一步：一趟扫描 List (每个元素只访问 1 次)
        // ==========================================
        for (i in 0 until actualBars) {
            val start = (i * bucketSize).toInt()
            val end = ((i + 1) * bucketSize).toInt().coerceAtMost(values.size)

            var bucketMaxInt = 0

            for (j in start until end) {
                // 这里发生了唯一一次从 List 中 GET 元素和拆箱(Unboxing)
                val v = values[j]

                // 累加平方和与求极值
                globalSumSq += v.toLong() * v.toLong()

                if (v > globalMaxInt) globalMaxInt = v
                if (v > bucketMaxInt) bucketMaxInt = v
            }
            rawDownsampled[i] = bucketMaxInt
        }

        // ==========================================
        // 第二步：循环结束，开始做浮点和视觉计算
        // ==========================================
        val globalMax = globalMaxInt.toFloat()
        val rms = sqrt(globalSumSq.toDouble() / values.size).toFloat()
        val reference = max(rms, if (globalMax > 0f) globalMax else 1f)

        val threshold = 0.15f

        for (i in 0 until actualBars) {
            val rawBar = rawDownsampled[i].toFloat()

            val norm = (rawBar / reference).coerceIn(0f, 1f)
            val gammaCorrected = norm.pow(0.618f)

            val finalValue = if (gammaCorrected < threshold) {
                0.01f
            } else {
                val mapped = 0.1f + (gammaCorrected - threshold) * (1f - 0.1f) / (1f - threshold)
                mapped.coerceIn(0.1f, 1f)
            }

            result[i] = finalValue
        }

        return result
    }

    private companion object {
        const val DEFAULT_MAX_BARS = -1
        const val BARS_PER_SECOND = 4f
        const val MIN_BARS = 120
        const val MAX_BARS = 30000  // 支持最多 ~2 小时 (30000 / 4 = 7500秒 ≈ 125分钟)
        const val FALLBACK_BARS = 256
    }
}
