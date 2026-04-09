package com.example.podcastapp.core.audioprocessing

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.linc.amplituda.Amplituda
import com.linc.amplituda.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class WaveformGenerator(
    private val context: Context,
) {
    private val amplituda: Amplituda by lazy { Amplituda(context) }

    suspend fun generate(uri: Uri, maxBars: Int = DEFAULT_MAX_BARS): List<Float> = withContext(Dispatchers.IO) {
        val (source, tempFile) = resolveSource(uri) ?: return@withContext emptyList()
        try {
            val durationMs = extractDurationMs(uri, source)
            val targetBars = if (maxBars > 0) maxBars else computeTargetBars(durationMs)
            val amplitudes = process(source)
            if (amplitudes.isEmpty()) return@withContext emptyList()
            val normalized = normalize2(amplitudes)
            downsample(normalized, targetBars)
        } finally {
            tempFile?.delete()
        }
    }

    private suspend fun process(source: String): List<Int> {
        val output = amplituda.processAudio(source, Cache.withParams(Cache.REUSE))
        return suspendCancellableCoroutine { cont ->
            output.get(
                { result -> cont.resume(result.amplitudesAsList()) },
                { error -> cont.resumeWithException(error) }
            )
        }
    }

    private fun resolveSource(uri: Uri): Pair<String, File?>? {
        return when (uri.scheme?.lowercase()) {
            "http", "https" -> uri.toString() to null
            "content" -> copyToCache(uri)
            "file" -> uri.path?.let { it to null }
            null -> uri.toString() to null
            else -> uri.toString() to null
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

    private fun normalize(values: List<Int>): List<Float> {
        val maxVal = values.maxOrNull()?.toFloat() ?: 0f
        if (maxVal <= 0f) return List(values.size) { 0f }

        return values.map { v ->
            // 使用根号或者 Math.pow 提升低振幅部分的视觉存在感（类似对数压缩）
            val norm = (v / maxVal).coerceIn(0f, 1f)
//            Math.sqrt(norm.toDouble()).toFloat()
            norm.toDouble().pow(2.0).toFloat()
        }
    }

    private fun normalize2(values: List<Int>): List<Float> {
        val rawValues = values.map { it.toFloat() }

        // 方案 A（最稳健）：计算全局均方根 (RMS)，并以此作为基准
        // 这个方案能非常好地解决由于现代录音压缩导致所有值都挤在顶部的问题。
        val sumSq = rawValues.sumOf { (it * it).toDouble() }
        val rms = if (values.isNotEmpty()) {
            sqrt(sumSq / values.size).toFloat()
        } else 0f

        if (rms <= 0f) return List(values.size) { 0f }

        // 使用非线性 Gamma 函数（类似于 Sqrt，但更好）拉伸动态范围
        // 我们让高度低于 RMS 的柱子在视觉上增长，高于 RMS 的柱子增长平缓。
        // 这能在高的柱子之间制造明显的低谷，拉开对比度。
        return rawValues.map { v ->
            // 使用一个基准系数，确保高的依然是最高，低的被压平
            val reference = maxOf(rms, rawValues.maxOrNull() ?: 1f)
            val norm = (v / reference).coerceIn(0f, 1f)

            // --- 核心修改： Gamma 矫正 ---
            // 指数越小（如 0.5 甚至 0.3），低的放大越多，整体看起来越平滑但有变化
            // 指数越大（如 1.2 甚至 1.5），高低对比越强烈
            // 建议尝试 0.618F（黄金比例，视觉上最舒适）
            Math.pow(norm.toDouble(), 0.618).toFloat()
        }
    }

    // 2. 采样：改用 RMS（均方根）或 Max（最大值）
    private fun downsample(values: List<Float>, maxBars: Int): List<Float> {
        if (values.size <= maxBars) return values

        val bucketSize = values.size.toFloat() / maxBars
        return (0 until maxBars).map { i ->
            val start = (i * bucketSize).toInt()
            val end = ((i + 1) * bucketSize).toInt().coerceAtMost(values.size)
            val slice = values.subList(start, maxOf(start + 1, end))

            // --- 核心修改：取最大值而不是平均值 ---
            // 最大值能保留音频的“骨架”和瞬态，让波形看起来更有力
            val barValue = slice.maxOrNull() ?: 0f

            val threshold = 0.15f // 低于 15% 的振幅将被视为“噪音”压低
            val finalValue = if (barValue < threshold) {
                // 低于阈值：给一个极小的保底（0.01），制造完全塌陷感
                0.01f
            } else {
                // 高于阈值：在线性基础上做一个动态增强，让它更“挺拔”
                // 将 [threshold, 1.0] 的范围重新映射到 [0.1, 1.0]
                val mapped = 0.1f + (barValue - threshold) * (1f - 0.1f) / (1f - threshold)
                mapped.coerceIn(0.1f, 1f)
            }
            finalValue
        }
    }

    private companion object {
        const val DEFAULT_MAX_BARS = -1
        const val BARS_PER_SECOND = 4f
        const val MIN_BARS = 120
        const val MAX_BARS = 30000  // 支持最多 ~2 小时 (30000 / 4 = 7500秒 ≈ 125分钟)
        const val FALLBACK_BARS = 256
    }
}
