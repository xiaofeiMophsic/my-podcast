package com.example.podcastapp.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class RssFetcher(
    private val httpClient: OkHttpClient,
    private val parser: RssParser,
) {
    suspend fun fetch(feedUrl: String): RssFeed = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(feedUrl).build()
        val call = httpClient.newCall(request)
        val response = call.execute()
        if (!response.isSuccessful) {
            response.close()
            throw IllegalStateException("HTTP ${response.code} for $feedUrl")
        }
        response.body?.byteStream().use { stream ->
            if (stream == null) throw IllegalStateException("Empty body for $feedUrl")
            parser.parse(stream)
        }
    }
}
