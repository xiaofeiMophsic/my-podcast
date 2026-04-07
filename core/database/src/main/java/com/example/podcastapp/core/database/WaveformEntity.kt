package com.example.podcastapp.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_waveforms")
data class EpisodeWaveformEntity(
    @PrimaryKey val episodeId: Long,
    val barsCsv: String,
    val updatedAt: Long,
)
