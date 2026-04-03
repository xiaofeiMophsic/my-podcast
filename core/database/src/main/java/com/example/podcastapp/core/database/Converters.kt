package com.example.podcastapp.core.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toDownloadStatus(value: String?): DownloadStatus? {
        return value?.let { DownloadStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus?): String? {
        return status?.name
    }
}
