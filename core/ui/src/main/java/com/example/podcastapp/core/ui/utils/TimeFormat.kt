package com.example.podcastapp.core.ui.utils

import kotlin.time.Duration


fun Duration.toPlaybackString(): String {
    fun Int.pad() = this.toString().padStart(2, '0')
    // toComponents 会把时长拆解，最后一个参数是纳秒（这里用不到，所以用 _）
    return toComponents { h, m, s, _ ->
        if (h > 0) "$h:${m.pad()}:${s.pad()}" else "$m:${s.pad()}"
    }
}
