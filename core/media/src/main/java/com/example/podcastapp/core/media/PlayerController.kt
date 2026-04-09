package com.example.podcastapp.core.media

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.podcastapp.core.audioprocessing.WaveformGenerator
import com.example.podcastapp.core.data.WaveformRepository
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    private val context: Context,
    private val waveformRepository: WaveformRepository,
    private val waveformGenerator: WaveformGenerator
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // 核心：UI 操作的是这个“远程遥控器”
    private var browser: MediaController? = null

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    init {
        setupMediaController()
    }

    private fun setupMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            val controller = controllerFuture.get()
            this.browser = controller

            // 监听播放器状态变化并更新给 UI
            controller.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateState(isPlaying = isPlaying)
                    handlePositionPolling(isPlaying) // 动态开关
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateState(durationMs = controller.duration.coerceAtLeast(0L))
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    updateState(
                        title = mediaMetadata.title?.toString(),
                        artist = mediaMetadata.artist?.toString(),
                        imageUrl = mediaMetadata.artworkUri?.toString()
                    )
                }
            })

            // 开启进度轮询
            startPositionPolling()
        }, MoreExecutors.directExecutor())
    }


    private var positionJob: Job? = null

    private fun handlePositionPolling(isPlaying: Boolean) {
        positionJob?.cancel() // 每次状态切换先清理旧任务
        if (isPlaying) {
            positionJob = scope.launch {
                while (isActive) {
                    val pos = browser?.currentPosition ?: 0L
                    // 只有当位置真的变了才更新 State，避免 Compose 重组浪费
                    if (_state.value.positionMs != pos) {
                        updateState(positionMs = pos)
                    }

                    // 16ms 对应 60fps，这是最适合你波形图平滑移动的频率
                    delay(16)
                }
            }
        }
    }

    private fun startPositionPolling() {
        scope.launch {
            while (isActive) {
                browser?.let {
                    if (it.isPlaying) {
                        updateState(positionMs = it.currentPosition.coerceAtLeast(0L))
                    }
                }
                delay(100) // 100ms 轮询一次进度
            }
        }
    }

    fun playEpisode(
        episodeId: Long,
        title: String,
        url: String,
        artist: String? = null,
        imageUrl: String? = null
    ) {
        val controller = browser ?: return

        // 如果当前正在播放同一个 episode，不需要重新加载，只需要确保播放
        if (_state.value.episodeId == episodeId && controller.isPlaying) {
            // 已经在播放同一个，直接返回
            return
        }
        if (_state.value.episodeId == episodeId && !controller.isPlaying) {
            // 同一个但是暂停了，直接继续播放
            controller.play()
            return
        }

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setArtworkUri(imageUrl?.toUri())
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(episodeId.toString())
            .setMediaMetadata(mediaMetadata)
            .build()

        // 发送指令给后台 Service
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()

        // 波形生成逻辑（异步，先查缓存）
        scope.launch(Dispatchers.IO) {
            // 先检查数据库缓存
            val cached = waveformRepository.getWaveform(episodeId)
            val bars = if (cached != null) {
                // 从缓存读取
                cached
            } else {
                // 没有缓存，重新生成
                updateState(isGeneratingWaveform = true)
                val generated = waveformGenerator.generate(url.toUri())
                // 保存到数据库
                waveformRepository.saveWaveform(episodeId, generated)
                generated
            }
            withContext(Dispatchers.Main) {
                updateState(
                    waveformBars = bars,
                    isGeneratingWaveform = false,
                    title = title,
                    artist = artist,
                    imageUrl = imageUrl,
                    episodeId = episodeId
                )
            }
        }
    }

    fun play() = browser?.play()
    fun pause() = browser?.pause()
    fun seekTo(positionMs: Long) = browser?.seekTo(positionMs)

    private fun updateState(
        isPlaying: Boolean? = null,
        title: String? = null,
        artist: String? = null,
        imageUrl: String? = null,
        positionMs: Long? = null,
        durationMs: Long? = null,
        episodeId: Long? = null,
        waveformBars: List<Float>? = null,
        isGeneratingWaveform: Boolean? = null,
    ) {
        val current = _state.value
        _state.value = current.copy(
            isPlaying = isPlaying ?: current.isPlaying,
            title = title ?: current.title,
            artist = artist ?: current.artist,
            imageUrl = imageUrl ?: current.imageUrl,
            positionMs = positionMs ?: current.positionMs,
            durationMs = durationMs ?: current.durationMs,
            episodeId = episodeId ?: current.episodeId,
            waveformBars = waveformBars ?: current.waveformBars,
            isGeneratingWaveform = isGeneratingWaveform ?: current.isGeneratingWaveform,
        )
    }
}