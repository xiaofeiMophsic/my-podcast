package com.example.podcastapp.core.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.media.PlayerController
import com.example.podcastapp.core.media.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controller: PlayerController,
) : ViewModel() {

    val state: StateFlow<PlayerState> = controller.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerState())

    fun playEpisode(episodeId: Long, title: String, url: String) {
        controller.playEpisode(episodeId, title, url)
    }

    fun togglePlayPause(isPlaying: Boolean) {
        if (isPlaying) controller.pause() else controller.play()
    }

    fun seekTo(positionMs: Long) {
        controller.seekTo(positionMs)
    }
}
