package com.example.podcastapp.core.player

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.media.MetadataState
import com.example.podcastapp.core.media.PlayerController
import com.example.podcastapp.core.media.PlayerState
import com.example.podcastapp.core.ui.utils.htmlToAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
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
    private val waveformRepository: com.example.podcastapp.core.data.WaveformRepository,
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = controller.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerState())

    val metadataState = playerState.map {
        MetadataState(
            title = it.title,
            artist = it.artist,
            imageUrl = it.imageUrl,
            durationMs = it.durationMs,
            episodeId = it.episodeId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MetadataState())

    val progressState = playerState.map { it.positionMs }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val playingState = playerState.map { it.isPlaying }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _waveformBars = MutableStateFlow<List<WaveBar>>(emptyList())
    val waveformBars: StateFlow<List<WaveBar>> = _waveformBars.asStateFlow()

    private val _isGeneratingWaveform = MutableStateFlow(true)
    val isGeneratingWaveform: StateFlow<Boolean> = _isGeneratingWaveform.asStateFlow()

    // Observe waveform changes when current episode changes
    init {
        playerState.map { it.episodeId }.distinctUntilChanged().onEach { episodeId ->
            if (episodeId == null) {
                _waveformBars.value = emptyList()
                _isGeneratingWaveform.value = false
            } else {
                // Start observing waveform from database
                waveformRepository.observeWaveform(episodeId)
                    .distinctUntilChanged()
                    .collect { bars ->
                        _waveformBars.value = bars.orEmpty().map { WaveBar(it) }
                        _isGeneratingWaveform.value = bars == null
                    }
            }
        }.launchIn(viewModelScope)
    }

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
                artist = episode.author,
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
                annotatedDesc = episode.description?.htmlToAnnotatedString(),
                imageUrl = episode.imageUrl,
                pubDate = episode.pubDate?.let { dateFormatter.format(Date(it)) },
            )
        }
    }

    fun playEpisode(
        episodeId: Long,
        title: String,
        url: String,
        artist: String? = null,
        imageUrl: String? = null
    ) {
        controller.playEpisode(episodeId, title, url, artist, imageUrl)
    }

    fun togglePlayPause() {
        if (playingState.value) controller.pause() else controller.play()
    }

    fun seekTo(positionMs: Long) {
        controller.seekTo(positionMs)
    }
}

@Immutable
data class NowPlayingEpisodeDetail(
    val id: Long? = null,
    val title: String = "",
    val description: String? = null,
    val annotatedDesc: AnnotatedString? = null,
    val imageUrl: String? = null,
    val pubDate: String? = null,
)
