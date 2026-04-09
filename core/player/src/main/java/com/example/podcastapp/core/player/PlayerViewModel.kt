package com.example.podcastapp.core.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.media.PlayerController
import com.example.podcastapp.core.media.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controller: PlayerController,
    private val episodeRepository: EpisodeRepository,
    private val downloadRepository: DownloadRepository,
) : ViewModel() {

    val state: StateFlow<PlayerState> = controller.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerState())

    private val _episodeDetail = MutableStateFlow(NowPlayingEpisodeDetail())
    val episodeDetail: StateFlow<NowPlayingEpisodeDetail> = _episodeDetail.asStateFlow()

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    fun playEpisodeById(episodeId: Long) {
        viewModelScope.launch {
            val episode = episodeRepository.getEpisode(episodeId) ?: return@launch
            val localPath = downloadRepository.getByEpisode(episodeId)?.localPath
            val playUrl = localPath ?: episode.audioUrl
            controller.playEpisode(
                episodeId = episode.id,
                title = episode.title,
                url = playUrl,
                imageUrl = episode.imageUrl,
            )
            refreshEpisodeDetail(episode.id)
        }
    }

    fun refreshEpisodeDetail(episodeId: Long?) {
        if (episodeId == null) {
            _episodeDetail.value = NowPlayingEpisodeDetail()
            return
        }
        viewModelScope.launch {
            val episode = episodeRepository.getEpisode(episodeId) ?: return@launch
            _episodeDetail.value = NowPlayingEpisodeDetail(
                id = episode.id,
                title = episode.title,
                description = episode.description,
                imageUrl = episode.imageUrl,
                pubDate = episode.pubDate?.let { dateFormatter.format(Date(it)) },
            )
        }
    }

    fun playEpisode(episodeId: Long, title: String, url: String, artist: String? = null, imageUrl: String? = null) {
        controller.playEpisode(episodeId, title, url, artist, imageUrl)
    }

    fun togglePlayPause(isPlaying: Boolean) {
        if (isPlaying) controller.pause() else controller.play()
    }

    fun seekTo(positionMs: Long) {
        controller.seekTo(positionMs)
    }
}

data class NowPlayingEpisodeDetail(
    val id: Long? = null,
    val title: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val pubDate: String? = null,
)
