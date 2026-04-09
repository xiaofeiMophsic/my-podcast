package com.example.podcastapp.feature.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.database.DownloadEntity
import com.example.podcastapp.core.database.DownloadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
) : ViewModel() {

    val uiState: StateFlow<DownloadScreenUiState> = downloadRepository.observeDownloads()
        .map { entities ->
            val items = entities.map { it.toUiState() }
            DownloadScreenUiState(
                activeItems = items.filter { it.isActive },
                historyItems = items.filter { !it.isActive },
                isEmpty = entities.isEmpty()
            )
        }
        .flowOn(Dispatchers.Default) // 在计算线程执行 filter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DownloadScreenUiState())
}

// 扩展函数：处理转换逻辑
private fun DownloadEntity.toUiState() = DownloadItemUiState(
    id = id,
    episodeId = episodeId,
    title = "Episode #$episodeId",
    status = status,
    statusLabel = when (status) {
        DownloadStatus.QUEUED -> "Queued"
        DownloadStatus.DOWNLOADING -> "Downloading"
        DownloadStatus.COMPLETED -> "Completed"
        DownloadStatus.FAILED -> "Failed"
        DownloadStatus.CANCELED -> "Canceled"
    },
    progress = when {
        totalBytes > 0L -> (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
        progress > 0 -> (progress / 100f).coerceIn(0f, 1f)
        else -> 0f
    },
    localPath = localPath,
    isActive = status == DownloadStatus.DOWNLOADING || status == DownloadStatus.QUEUED
)

// 只有 UI 需要的数据，且属性尽量简单
data class DownloadItemUiState(
    val id: Long,
    val episodeId: Long,
    val title: String,
    val status: DownloadStatus,
    val statusLabel: String,
    val progress: Float, // 预计算 0f-1f
    val localPath: String?,
    val isActive: Boolean
)

data class DownloadScreenUiState(
    val activeItems: List<DownloadItemUiState> = emptyList(),
    val historyItems: List<DownloadItemUiState> = emptyList(),
    val isEmpty: Boolean = true
)