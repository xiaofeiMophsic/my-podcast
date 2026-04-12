package com.example.podcastapp.core.player.wave

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.podcastapp.core.player.WaveBar
import com.example.podcastapp.core.ui.neo.NeoColors
import kotlin.random.Random

@Composable
fun AudioWaveform(
    waveformBars: FloatArray,
    progress: () -> Float,
    onSeekTo: (Float) -> Unit,
    onScrub: (Float) -> Unit,
    onScrubEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val barWidthPx = with(density) { 4.dp.toPx() }
    val spacingPx = with(density) { 3.dp.toPx() }

    val currentProgressProvider by rememberUpdatedState(progress)

    val animatedProgress by animateFloatAsState(
        targetValue = currentProgressProvider(),
        animationSpec = tween(durationMillis = 120, easing = LinearEasing),
        label = "waveformProgress",
    )

    Canvas(
        modifier = modifier.pointerInput(Unit) {

            fun updateSeekPosition(
                touchX: Float,
                currentProgress: Float,
            ) {
                val totalBars = waveformBars.size
                if (totalBars == 0) return
                val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
                val playheadX = currentProgress.coerceIn(0f, 1f) * totalWidth
                val centerX = size.width / 2f
                val minOffset = size.width - totalWidth
                val desiredOffset = centerX - playheadX
                val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)
                val positionX = (touchX - offsetX).coerceIn(0f, totalWidth)
                val fraction = (positionX / totalWidth).coerceIn(0f, 1f)
                onSeekTo(fraction)
                onScrub(fraction)
            }

            detectDragGestures(
                onDragStart = { offset ->
                    updateSeekPosition(offset.x, animatedProgress)
                },
                onDrag = { change, _ ->
                    updateSeekPosition(change.position.x, animatedProgress)
                },
                onDragEnd = { onScrubEnd() },
                onDragCancel = { onScrubEnd() },
            )
        }
    ) {

        val currentProgress = animatedProgress

        val totalBars = waveformBars.size
        if (totalBars == 0) return@Canvas
        val maxHeightPx = size.height
        val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
        val clampedProgress = currentProgress.coerceIn(0f, 1f)
        val playheadX = clampedProgress * totalWidth

        val centerX = size.width / 2f
        val minOffset = size.width - totalWidth
        val desiredOffset = centerX - playheadX
        val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)

        waveformBars.forEachIndexed { index, bar ->
            val absoluteX = index * (barWidthPx + spacingPx)
            val x = absoluteX + offsetX
            val barH = (bar * maxHeightPx * 1.2f).coerceAtMost(maxHeightPx)
            val y = (maxHeightPx - barH) / 2f
            // 计算当前这条 Bar 的左右边界（相对于音频总长度的绝对坐标）
            val barLeft = absoluteX
            val barRight = absoluteX + barWidthPx

            when {
                // 情况 1: 播放头已经完全覆盖该 Bar -> 全绿
                barRight <= playheadX -> {
                    drawRoundRect(
                        color = NeoColors.AccentGreen,
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }
                // 情况 2: 播放头还没到该 Bar -> 全灰
                barLeft >= playheadX -> {
                    drawRoundRect(
                        color = NeoColors.LightGray,
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }
                // 情况 3: 播放头正处于该 Bar 中间 -> 丝滑渐变点亮
                else -> {
                    // 计算播放头在该 Bar 内部的相对比例 (0.0 ~ 1.0)
                    val internalProgress = (playheadX - barLeft) / barWidthPx

                    // 使用 Brush.linearGradient 实现左绿右灰的硬切分
                    // 通过 stop 相同来实现没有模糊的平滑切割点
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            0f to NeoColors.AccentGreen,
                            internalProgress to NeoColors.AccentGreen,
                            internalProgress to NeoColors.LightGray,
                            1f to NeoColors.LightGray,
                            startX = x,
                            endX = x + barWidthPx
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }
            }
        }
    }
}

@Composable
fun WaveformLoading(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    // Base random heights - fixed random pattern
    val baseHeights = remember {
        List(50) { i ->
            // Use position-based pseudo-random for consistency
            val noise = Random(i).nextFloat()
            0.25f + noise * 0.45f // 25% ~ 70% base height range
        }
    }

    val dp8 = with(LocalDensity.current) { 8.dp.toPx() }
    // Base random vertical offsets
    val baseOffsets = remember {
        List(50) { i ->
            val noise = Random(i + 1000).nextFloat()
            (noise - 0.5f) * dp8 // -4dp ~ +4dp static random
        }
    }

    Canvas(modifier = modifier) {
        val barWidthPx = 4.dp.toPx()
        val spacingPx = 3.dp.toPx()
        val maxHeight = size.height

        repeat(50) { i ->
            val x = i * (barWidthPx + spacingPx)
            // Phase offset increases from left to right
            // time - phase creates wave traveling to the right
            // left bars move first, right bars follow - correct direction
            val phase = (i / 50f) * 2 * kotlin.math.PI.toFloat()
            // Each bar height animates independently - looks like sound wave moving right
            val heightNorm = baseHeights[i] + 0.2f * kotlin.math.sin(time - phase)
            val barHeight = heightNorm.coerceIn(0.15f, 0.85f) * maxHeight
            val baseY = (maxHeight - barHeight) / 2f
            val y = baseY + baseOffsets[i]

            // Constant alpha - height animation creates the wave movement effect
            drawRoundRect(
                color = NeoColors.LightGray.copy(alpha = 0.5f),
                topLeft = Offset(x, y),
                size = Size(barWidthPx, barHeight),
                cornerRadius = CornerRadius(barWidthPx / 2f)
            )
        }
    }
}