package com.example.podcastapp.feature.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.data.PodcastRepository
import com.example.podcastapp.core.database.PodcastEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastListViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val episodeRepository: EpisodeRepository,
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<PodcastListUiState> =
        combine(
            podcastRepository.observePodcasts(),
            podcastRepository.observeSubscriptions(),
            _error,
            _isLoading,
        ) { podcasts, subscriptions, error, isLoading ->
            val subscribedIds = subscriptions.map { it.podcastId }.toSet()
            PodcastListUiState(
                isLoading = isLoading,
                errorMessage = error,
                items = podcasts.map { it.toUi(subscribedIds.contains(it.id)) },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PodcastListUiState())

    fun addSubscription(feedUrl: String) {
        val trimmed = feedUrl.trim()
        if (trimmed.isBlank()) {
            _error.value = "Feed URL is required"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val podcast = podcastRepository.refreshPodcast(trimmed)
                podcastRepository.subscribe(podcast.id)
                episodeRepository.refreshEpisodes(podcast.id)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to add feed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

private fun PodcastEntity.toUi(isSubscribed: Boolean): PodcastUi {
    return PodcastUi(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        isSubscribed = isSubscribed,
    )
}
