package com.example.podcastapp.feature.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.DownloadController
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.database.DownloadStatus
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.media.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EpisodeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val episodeRepository: EpisodeRepository,
    private val downloadController: DownloadController,
    private val downloadRepository: DownloadRepository,
    private val playerController: PlayerController,
) : ViewModel() {

    private val episodeId: Long = checkNotNull(savedStateHandle["episodeId"])

    private val _state = MutableStateFlow(EpisodeDetailUiState())
    val state: StateFlow<EpisodeDetailUiState> = _state.asStateFlow()

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val playerState = playerController.state

    init {
        viewModelScope.launch {
            val episode = episodeRepository.getEpisode(episodeId)
            _state.value = EpisodeDetailUiState(
                title = episode?.title ?: "",
                description = episode?.description,
                pubDate = episode?.pubDate?.let { dateFormatter.format(Date(it)) },
                audioUrl = episode?.audioUrl,
                imageUrl = episode?.imageUrl,
                episodeId = episode?.id,
                downloadStatus = DownloadStatus.QUEUED,
                localPath = null,
            )
        }
        observeDownload()
    }

    private fun observeDownload() {
        viewModelScope.launch {
            downloadRepository.observeDownloads().collect { items ->
                val current = items.firstOrNull { it.episodeId == episodeId } ?: return@collect
                _state.value = _state.value.copy(
                    downloadStatus = current.status,
                    localPath = current.localPath,
                )
            }
        }
    }

    fun download() {
        val current = _state.value
        val id = current.episodeId ?: return
        val url = current.audioUrl ?: return
        val title = current.title.ifBlank { "episode_$id" }
        viewModelScope.launch {
            downloadController.enqueue(id, title, url)
        }
    }

    fun play() {
        val current = _state.value
        val id = current.episodeId ?: return
        val url = current.localPath ?: current.audioUrl ?: return
        val title = current.title
        playerController.playEpisode(id, title, url, imageUrl = current.imageUrl)
    }

    fun togglePlay() {
        if (playerState.value.isPlaying) {
            playerController.pause()
        } else {
            val current = _state.value
            val id = current.episodeId ?: return
            if (playerState.value.episodeId == id) {
                playerController.play()
            } else {
                play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
    }

    fun skipForward() {
        val current = playerController.state.value.positionMs
        playerController.seekTo(current + 30000)
    }

    fun skipBackward() {
        val current = playerController.state.value.positionMs
        playerController.seekTo((current - 10000).coerceAtLeast(0))
    }
}

data class EpisodeDetailUiState(
    val title: String = "",
    val description: String? = null,
    val pubDate: String? = null,
    val audioUrl: String? = null,
    val imageUrl: String? = null,
    val episodeId: Long? = null,
    val downloadStatus: DownloadStatus = DownloadStatus.QUEUED,
    val localPath: String? = null,
)
