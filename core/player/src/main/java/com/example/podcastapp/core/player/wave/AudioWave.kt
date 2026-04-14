package com.example.podcastapp.core.player.wave

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.podcastapp.core.ui.neo.NeoColors
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
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

    // 用来接管拖拽时的瞬时进度
    var localScrubFraction by remember { mutableStateOf<Float?>(null) }
    val currentPlaybackProgress by rememberUpdatedState(progress())

    // 🚨 新增：用来在松手后冻结画面的”预期进度”
    var expectedSeekProgress by remember { mutableStateOf<Float?>(null) }

    // 🌟 新增特效 1/3：记录是否有手指按在屏幕上
    var isPressing by remember { mutableStateOf(false) }

    // 惯性滑动：Animatable + coroutine scope
    val flingAnimatable = remember { Animatable(0f) }
    val flingScope = rememberCoroutineScope()
    val splineDecay = androidx.compose.foundation.gestures.rememberSplineBasedDecay<Float>()

    // 震动反馈：追踪上一根经过的柱子索引
    val view = LocalView.current
    var lastHapticBarIndex by remember { mutableIntStateOf(-1) }

    // 只要是在按下、拖拽、或惯性滑动中，就是”交互中”
    val isInteracting = isPressing || localScrubFraction != null

    // 🌟 核心特效引擎：带弹性阻尼的放大系数 (1.0倍 -> 1.15倍)
    val waveScale by animateFloatAsState(
        targetValue = if (isInteracting) 1.3f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = 0.5f, // 阻尼系数：越小弹簧感越强 (0.5具有完美的Q弹感)
            stiffness = 300f     // 弹簧刚度：控制放大缩小的速度
        ),
        label = "waveScale",
    )

    // 🚨 新增：启动一个 500ms 的保护期。
    // 这段时间足够底层播放器完成真实 seek，500ms 后撤销冻结，让 UI 重新跟底层真实进度挂钩。
    LaunchedEffect(expectedSeekProgress) {
        if (expectedSeekProgress != null) {
            delay(500)
            expectedSeekProgress = null
        }
    }

    // 只在正常播放时应用动画。拖拽时，跳过动画以实现完全跟手的 0 延迟。
    val animatedProgress by animateFloatAsState(
        targetValue = currentPlaybackProgress,
        animationSpec = tween(durationMillis = 120, easing = LinearEasing),
        label = "waveformProgress",
    )

    // 最核心的决策判断：如果在拖拽，强制使用手势计算出的进度
    val renderProgress = localScrubFraction ?: expectedSeekProgress ?: animatedProgress
    // 🚨 新增这一行：将最新计算出的进度包装成永不过期的 State
    val latestRenderProgress by rememberUpdatedState(renderProgress)

    Canvas(
        modifier = modifier
//            .clipToBounds()
            .graphicsLayer {
                scaleY = waveScale // 高度放大 1.15 倍
                scaleX = 1f + (waveScale - 1f) * 0.3f // 宽度仅微微放大，防止磁带偏移
            }
            .pointerInput(waveformBars.size) { // 当音频改变时重置手势
                coroutineScope {
                    // 1. 监听点击手势 (Tap)：如果你想要点哪跳哪，就留着这个；不要可以删掉这块 launch
                    launch {
                        detectTapGestures(
                            onTap = { tapOffset ->
                                val totalBars = waveformBars.size
                                if (totalBars == 0) return@detectTapGestures
                                val totalWidth =
                                    totalBars * barWidthPx + (totalBars - 1) * spacingPx

                                val playheadX = latestRenderProgress * totalWidth
                                val centerX = size.width / 2f
                                val minOffset = size.width - totalWidth
                                val desiredOffset = centerX - playheadX
                                val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(
                                    minOffset,
                                    0f
                                )

                                val clickedAbsoluteX = tapOffset.x - offsetX
                                val newProgress = (clickedAbsoluteX / totalWidth).coerceIn(0f, 1f)

                                expectedSeekProgress = newProgress
                                onSeekTo(newProgress)
                            },

                            onPress = {
                                isPressing = true
                                tryAwaitRelease() // 等待手指抬起
                                isPressing = false
                            }
                        )
                    }

                    // 2. 监听水平拖拽手势 (Drag)：将手势位移转化为磁带拨动效果 + 惯性滑动
                    launch {
                        val velocityTracker = VelocityTracker()

                        detectHorizontalDragGestures(
                            onDragStart = {
                                // 取消正在进行的惯性滑动
                                flingAnimatable.stop()
                                // 刚按下去时，把当前画面停留的进度记录下来作为基准点
                                localScrubFraction = latestRenderProgress
                                lastHapticBarIndex = if (waveformBars.isNotEmpty()) {
                                    (latestRenderProgress * waveformBars.size).toInt()
                                        .coerceIn(0, waveformBars.size - 1)
                                } else -1
                                velocityTracker.resetTracking()
                                onScrub(latestRenderProgress)
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val totalBars = waveformBars.size
                                if (totalBars == 0) return@detectHorizontalDragGestures
                                val totalWidth =
                                    totalBars * barWidthPx + (totalBars - 1) * spacingPx

                                // 记录速度用于惯性滑动
                                velocityTracker.addPosition(
                                    change.uptimeMillis,
                                    change.position
                                )

                                val deltaProgress = -dragAmount / totalWidth

                                localScrubFraction?.let { current ->
                                    val newProgress = (current + deltaProgress).coerceIn(0f, 1f)
                                    localScrubFraction = newProgress
                                    onScrub(newProgress)

                                    // 震动反馈：每划过一根柱子轻微震动
                                    val currentBarIndex = (newProgress * totalBars).toInt()
                                        .coerceIn(0, totalBars - 1)
                                    if (currentBarIndex != lastHapticBarIndex) {
                                        lastHapticBarIndex = currentBarIndex
                                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                    }
                                }
                            },
                            onDragEnd = {
                                val currentFraction = localScrubFraction ?: return@detectHorizontalDragGestures
                                val totalBars = waveformBars.size
                                if (totalBars == 0) {
                                    localScrubFraction = null
                                    onScrubEnd()
                                    return@detectHorizontalDragGestures
                                }
                                val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx

                                // 获取手指抬起时的像素速度，转换为 progress/秒
                                val pixelVelocity = velocityTracker.calculateVelocity().x
                                val progressVelocity = -pixelVelocity / totalWidth

                                // 如果速度很小，直接 seek 不做惯性
                                if (kotlin.math.abs(progressVelocity) < 0.05f) {
                                    expectedSeekProgress = currentFraction
                                    onSeekTo(currentFraction)
                                    localScrubFraction = null
                                    onScrubEnd()
                                    return@detectHorizontalDragGestures
                                }

                                // 启动惯性滑动动画
                                flingScope.launch {
                                    flingAnimatable.snapTo(currentFraction)
                                    flingAnimatable.updateBounds(lowerBound = 0f, upperBound = 1f)

                                    flingAnimatable.animateDecay(
                                        initialVelocity = progressVelocity,
                                        animationSpec = splineDecay,
                                    ) {
                                        localScrubFraction = value
                                        onScrub(value)

                                        // 惯性滑动中也触发震动
                                        val barIndex = (value * totalBars).toInt()
                                            .coerceIn(0, totalBars - 1)
                                        if (barIndex != lastHapticBarIndex) {
                                            lastHapticBarIndex = barIndex
                                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                        }
                                    }

                                    // 惯性结束，触发真正的 seek
                                    localScrubFraction?.let {
                                        expectedSeekProgress = it
                                        onSeekTo(it)
                                    }
                                    localScrubFraction = null
                                    onScrubEnd()
                                }
                            },
                            onDragCancel = {
                                flingAnimatable.stop()
                                localScrubFraction = null
                                onScrubEnd()
                            }
                        )
                    }
                }
            }
    ) {
        val totalBars = waveformBars.size
        if (totalBars == 0) return@Canvas

        val maxHeightPx = size.height
        val totalWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx

        val clampedProgress = latestRenderProgress.coerceIn(0f, 1f)
        val playheadX = clampedProgress * totalWidth

        val centerX = size.width / 2f
        val minOffset = size.width - totalWidth
        val desiredOffset = centerX - playheadX
        val offsetX = if (minOffset > 0f) 0f else desiredOffset.coerceIn(minOffset, 0f)

        // 🚨 关键修复 2：计算当前 Canvas 真实可视的物理边界
        val viewportLeft = 0f
        val viewportRight = size.width

        waveformBars.forEachIndexed { index, bar ->
            val absoluteX = index * (barWidthPx + spacingPx)
            val x = absoluteX + offsetX // 柱子在屏幕上的实际渲染 X 坐标
            val barRightX = x + barWidthPx

            // 🚀 性能大杀器：视口剔除 (Culling)
            // 如果这根柱子完全在屏幕左边，或者完全在屏幕右边，直接跳过，根本不交给底层 GPU！
            // 当音频有几千个点时，这能让渲染性能瞬间提升几十倍！
            if (barRightX < viewportLeft || x > viewportRight) {
                return@forEachIndexed
            }

            val barH = (bar * maxHeightPx * 1.2f).coerceAtMost(maxHeightPx)
            val y = (maxHeightPx - barH) / 2f

            // 这里的相对坐标 (barLeft vs playheadX) 用于颜色判断
            val barLeft = absoluteX
            val barRight = absoluteX + barWidthPx

            // 原本完美的渲染判断逻辑不变
            when {
                barRight <= playheadX -> {
                    drawRoundRect(
                        color = NeoColors.AccentGreen,
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }

                barLeft >= playheadX -> {
                    drawRoundRect(
                        color = NeoColors.LightGray,
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx, barH),
                        cornerRadius = CornerRadius(barWidthPx / 2f)
                    )
                }

                else -> {
                    val internalProgress = (playheadX - barLeft) / barWidthPx
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