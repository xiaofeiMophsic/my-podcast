package com.example.podcastapp.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_waveforms")
data class EpisodeWaveformEntity(
    @PrimaryKey val episodeId: Long,
    val barsBlob: ByteArray,
    val updatedAt: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EpisodeWaveformEntity
        return episodeId == other.episodeId && barsBlob.contentEquals(other.barsBlob) && updatedAt == other.updatedAt
    }

    override fun hashCode(): Int {
        var result = episodeId.hashCode()
        result = 31 * result + barsBlob.contentHashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}
