package com.example.podcastapp.core.media

import android.content.Context
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

class WaveformGenerator(
    private val context: Context,
) {
    private val amplituda: Amplituda by lazy { Amplituda(context) }

    suspend fun generate(uri: Uri, maxBars: Int = 64): List<Float> = withContext(Dispatchers.IO) {
        val (source, tempFile) = resolveSource(uri) ?: return@withContext emptyList()
        try {
            val amplitudes = process(source)
            if (amplitudes.isEmpty()) return@withContext emptyList()
            val normalized = normalize(amplitudes)
            downsample(normalized, maxBars)
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

    private fun normalize(values: List<Int>): List<Float> {
        var maxValue = 0f
        for (v in values) {
            maxValue = max(maxValue, v.toFloat())
        }
        if (maxValue <= 0f) return values.map { 0f }
        return values.map { (it / maxValue).coerceIn(0f, 1f) }
    }

    private fun downsample(values: List<Float>, maxBars: Int): List<Float> {
        if (values.size <= maxBars) return values.map { it.coerceIn(0f, 1f) }
        val bucketSize = values.size.toFloat() / maxBars
        val raw = (0 until maxBars).map { i ->
            val start = (i * bucketSize).toInt()
            val end = ((i + 1) * bucketSize).toInt().coerceAtMost(values.size)
            val slice = values.subList(start, end.coerceAtLeast(start + 1))
            (slice.sum() / slice.size).coerceIn(0f, 1f)
        }
        return smooth(raw, alpha = 0.2f)
    }

    private fun smooth(values: List<Float>, alpha: Float): List<Float> {
        if (values.isEmpty()) return values
        val out = ArrayList<Float>(values.size)
        var prev = values.first()
        out.add(prev)
        for (i in 1 until values.size) {
            val v = values[i]
            val smoothed = (prev + (v - prev) * alpha).coerceIn(0f, 1f)
            out.add(smoothed)
            prev = smoothed
        }
        return out
    }
}
