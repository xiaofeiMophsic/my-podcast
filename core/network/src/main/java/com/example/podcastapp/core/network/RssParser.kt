package com.example.podcastapp.core.network

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class RssParser {
    fun parse(input: InputStream): RssFeed {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(input, null)

        var event = parser.eventType
        var inItem = false

        var channelTitle: String? = null
        var channelDescription: String? = null
        var channelImageUrl: String? = null
        var channelAuthor: String? = null
        var channelLanguage: String? = null
        var channelLastBuild: Long? = null

        var itemTitle: String? = null
        var itemDescription: String? = null
        var itemGuid: String? = null
        var itemAudioUrl: String? = null
        var itemImageUrl: String? = null
        var itemDuration: Long? = null
        var itemPubDate: Long? = null

        val items = mutableListOf<RssItem>()

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.name?.lowercase(Locale.US) ?: ""
                    when (tag) {
                        "item" -> {
                            inItem = true
                            itemTitle = null
                            itemDescription = null
                            itemGuid = null
                            itemAudioUrl = null
                            itemImageUrl = null
                            itemDuration = null
                            itemPubDate = null
                        }
                        "title" -> {
                            val text = parser.nextTextOrNull()
                            if (inItem) itemTitle = text else channelTitle = text
                        }
                        "description" -> {
                            val text = parser.nextTextOrNull()
                            if (inItem) itemDescription = text else channelDescription = text
                        }
                        "guid" -> if (inItem) itemGuid = parser.nextTextOrNull()
                        "pubdate" -> if (inItem) itemPubDate = parseRfc822(parser.nextTextOrNull())
                        "lastbuilddate" -> if (!inItem) channelLastBuild = parseRfc822(parser.nextTextOrNull())
                        "language" -> if (!inItem) channelLanguage = parser.nextTextOrNull()
                        "author" -> if (!inItem) channelAuthor = parser.nextTextOrNull()
                        "enclosure" -> if (inItem) {
                            val url = parser.getAttributeValue(null, "url")
                            if (!url.isNullOrBlank()) itemAudioUrl = url
                        }
                        "itunes:image" -> {
                            val href = parser.getAttributeValue(null, "href")
                            if (!href.isNullOrBlank()) {
                                if (inItem) itemImageUrl = href else channelImageUrl = href
                            }
                        }
                        "media:content", "media:thumbnail" -> {
                            val url = parser.getAttributeValue(null, "url")
                            if (!url.isNullOrBlank()) {
                                if (inItem) itemImageUrl = url else if (channelImageUrl == null) channelImageUrl = url
                            }
                        }
                        "image" -> {
                            // Can be used for both channel and item
                        }
                        "url" -> if (parser.depth >= 3) {
                            val text = parser.nextTextOrNull()
                            if (!text.isNullOrBlank()) {
                                if (inItem) {
                                    if (itemImageUrl == null) itemImageUrl = text
                                } else {
                                    if (channelImageUrl == null) channelImageUrl = text
                                }
                            }
                        }
                        "itunes:author" -> if (!inItem) channelAuthor = parser.nextTextOrNull()
                        "itunes:duration" -> if (inItem) itemDuration = parseDuration(parser.nextTextOrNull())
                    }
                }
                XmlPullParser.END_TAG -> {
                    val tag = parser.name?.lowercase(Locale.US) ?: ""
                    if (tag == "item") {
                        inItem = false
                        val audio = itemAudioUrl
                        val guid = itemGuid ?: itemTitle ?: audio ?: ""
                        val title = itemTitle ?: "Untitled"
                        if (!audio.isNullOrBlank()) {
                            items.add(
                                RssItem(
                                    guid = guid,
                                    title = title,
                                    description = itemDescription,
                                    audioUrl = audio,
                                    imageUrl = itemImageUrl,
                                    durationSeconds = itemDuration,
                                    pubDate = itemPubDate,
                                )
                            )
                        }
                    }
                }
            }
            event = parser.next()
        }

        val title = channelTitle ?: "Podcast"
        return RssFeed(
            title = title,
            description = channelDescription,
            imageUrl = channelImageUrl,
            author = channelAuthor,
            language = channelLanguage,
            lastBuildDate = channelLastBuild,
            items = items,
        )
    }
}

private fun XmlPullParser.nextTextOrNull(): String? {
    return try {
        nextText()?.trim()?.ifBlank { null }
    } catch (_: Exception) {
        null
    }
}

private fun parseRfc822(value: String?): Long? {
    if (value.isNullOrBlank()) return null
    return try {
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.US)
        ZonedDateTime.parse(value, formatter).withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli()
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun parseDuration(value: String?): Long? {
    if (value.isNullOrBlank()) return null
    val trimmed = value.trim()
    return when {
        trimmed.contains(":") -> {
            val parts = trimmed.split(":")
            val numbers = parts.mapNotNull { it.toLongOrNull() }
            if (numbers.size != parts.size) return null
            when (numbers.size) {
                3 -> numbers[0] * 3600 + numbers[1] * 60 + numbers[2]
                2 -> numbers[0] * 60 + numbers[1]
                1 -> numbers[0]
                else -> null
            }
        }
        else -> trimmed.toLongOrNull()
    }
}
