package com.example.podcastapp.feature.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.database.EpisodeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val results: Flow<PagingData<EpisodeEntity>> = _query
        .debounce(300)
        .filter { it.isNotBlank() }
        .flatMapLatest { q ->
            Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = { episodeRepository.searchPaging(q) },
            ).flow
        }
        .cachedIn(viewModelScope)

    val isEmptyQuery: StateFlow<Boolean> = _query
        .map { it.isBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun updateQuery(value: String) {
        _query.value = value
    }
}
