package com.example.podcastapp.core.media

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    // 真正的播放器实例，由 Service 生命周期管理
    private lateinit var player: ExoPlayer

    @UnstableApi
    override fun onCreate() {
        super.onCreate()

        // 1. 初始化真实的 ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // 2. 创建点击通知栏跳转回 App 的 Intent
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. 将 Player 绑定到 Session
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    // 系统媒体中心或其他 Controller 连接时，返回这个 Session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        // 当用户在多任务卡片划掉 App 时，如果没在播放，就关掉服务
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}