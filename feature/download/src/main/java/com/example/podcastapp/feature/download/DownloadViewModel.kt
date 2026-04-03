package com.example.podcastapp.feature.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.database.DownloadEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
) : ViewModel() {

    val downloads: StateFlow<List<DownloadEntity>> = downloadRepository.observeDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
