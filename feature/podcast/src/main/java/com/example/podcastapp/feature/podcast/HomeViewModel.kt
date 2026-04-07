package com.example.podcastapp.feature.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.data.PodcastRepository
import com.example.podcastapp.core.data.WaveformRepository
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.database.PodcastEntity
import com.example.podcastapp.core.media.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val EPISODE_PREVIEW_LIMIT = 6

@HiltViewModel
class HomeViewModel @Inject constructor(
    podcastRepository: PodcastRepository,
    private val episodeRepository: EpisodeRepository,
    private val playerController: PlayerController,
    private val waveformRepository: WaveformRepository,
) : ViewModel() {

    private val subscribedPodcasts: Flow<List<PodcastEntity>> =
        combine(
            podcastRepository.observePodcasts(),
            podcastRepository.observeSubscriptions(),
        ) { podcasts, subs ->
            val subscribedIds = subs.map { it.podcastId }.toSet()
            podcasts.filter { subscribedIds.contains(it.id) }
        }

    val uiState = subscribedPodcasts
        .map { podcasts ->
            val sections = podcasts.map { podcast ->
                PodcastSection(
                    podcast = podcast,
                    episodesFlow = episodeRepository.observeLatestEpisodes(podcast.id, EPISODE_PREVIEW_LIMIT),
                )
            }
            HomeUiState(sections = sections)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun playEpisode(episode: EpisodeEntity) {
        viewModelScope.launch {
            val waveform = waveformRepository.getWaveform(episode.id)
            playerController.setStaticWaveform(waveform)
            playerController.playEpisode(
                episodeId = episode.id,
                title = episode.title,
                url = episode.audioUrl,
                imageUrl = episode.imageUrl,
            )
        }
    }
}

data class HomeUiState(
    val sections: List<PodcastSection> = emptyList(),
)

data class PodcastSection(
    val podcast: PodcastEntity,
    val episodesFlow: Flow<List<EpisodeEntity>>,
)
