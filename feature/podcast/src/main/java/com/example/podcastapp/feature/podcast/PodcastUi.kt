package com.example.podcastapp.feature.podcast

data class PodcastUi(
    val id: Long = 0,
    val title: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val isSubscribed: Boolean = false,
)

data class PodcastListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val items: List<PodcastUi> = emptyList(),
)
