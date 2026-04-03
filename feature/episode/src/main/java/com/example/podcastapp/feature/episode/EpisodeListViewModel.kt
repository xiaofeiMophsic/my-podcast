package com.example.podcastapp.feature.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.PodcastRepository
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.database.EpisodeEntity
import com.example.podcastapp.core.media.PlayerController
import com.example.podcastapp.core.media.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val episodeRepository: EpisodeRepository,
    private val podcastRepository: PodcastRepository,
    private val downloadRepository: DownloadRepository,
    private val playerController: PlayerController,
) : ViewModel() {

    private val podcastId: Long = checkNotNull(savedStateHandle["podcastId"]) as Long

    private val _title = MutableStateFlow("Episodes")
    val title: StateFlow<String> = _title.asStateFlow()

    val playerState: StateFlow<PlayerState> = playerController.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerState())

    val paging: Flow<PagingData<EpisodeEntity>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { episodeRepository.pagingByPodcast(podcastId) },
    ).flow.cachedIn(viewModelScope)

    val downloadStatusMap: StateFlow<Map<Long, DownloadStatus>> =
        downloadRepository.observeDownloads()
            .map { list -> list.associate { it.episodeId to it.status } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    init {
        viewModelScope.launch {
            val podcast = podcastRepository.getPodcast(podcastId)
            _title.value = podcast?.title ?: "Episodes"
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            episodeRepository.refreshEpisodes(podcastId)
        }
    }
}
