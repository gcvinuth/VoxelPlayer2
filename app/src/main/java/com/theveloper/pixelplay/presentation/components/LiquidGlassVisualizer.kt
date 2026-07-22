package com.theveloper.pixelplay.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

enum class LiquidVisualizerMode {
    WAVE, BARS, GLOW
}

/**
 * Jetpack Compose Liquid Glass Canvas Visualizer rendering Wave, Bars, and Glow forms.
 */
@Composable
fun LiquidGlassVisualizer(
    mode: LiquidVisualizerMode,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizerPhase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Math.PI.toFloat() * 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseAnimation"
    )

    Box(
        modifier = modifier
            .liquidGlass(
                shape = CircleShape,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                borderColor = Color.White.copy(alpha = 0.3f),
                elevation = 8.dp
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (mode) {
                LiquidVisualizerMode.WAVE -> {
                    val centerY = height / 2f
                    val path = Path()
                    val fillPath = Path()

                    fillPath.moveTo(0f, height)
                    path.moveTo(0f, centerY)

                    val points = 30
                    val sliceWidth = width / points

                    for (i in 0..points) {
                        val x = i * sliceWidth
                        val waveOffset = if (isPlaying) {
                            sin(phase + i * 0.35f) * (height * 0.28f)
                        } else {
                            sin(phase * 0.5f + i * 0.2f) * 8f
                        }
                        val y = centerY + waveOffset

                        if (i == 0) {
                            path.moveTo(x, y)
                            fillPath.lineTo(x, y)
                        } else {
                            val prevX = (i - 1) * sliceWidth
                            val controlX = (prevX + x) / 2f
                            path.quadraticTo(prevX, y, controlX, y)
                            fillPath.quadraticTo(prevX, y, controlX, y)
                        }
                    }

                    fillPath.lineTo(width, height)
                    fillPath.lineTo(0f, height)
                    fillPath.close()

                    // Fill under wave with translucent liquid gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )

                    // Draw Specular Crest Line
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White,
                                accentColor,
                                Color.White
                            )
                        ),
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                LiquidVisualizerMode.BARS -> {
                    val barCount = 18
                    val gap = 8.dp.toPx()
                    val totalGap = (barCount - 1) * gap
                    val barWidth = (width - totalGap) / barCount
                    val cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)

                    for (i in 0 until barCount) {
                        val amplitude = if (isPlaying) {
                            (sin(phase + i * 0.4f) + 1.1f) / 2.2f
                        } else {
                            0.15f
                        }
                        val barHeight = Math.max(16f, amplitude * (height * 0.85f))
                        val x = i * (barWidth + gap)
                        val y = height - barHeight

                        // Liquid Glass Bar Body
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    accentColor.copy(alpha = 0.7f),
                                    accentColor.copy(alpha = 0.3f)
                                ),
                                startY = y,
                                endY = height
                            ),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = cornerRadius
                        )

                        // Specular Highlight Top Cap
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color.White.copy(alpha = 0.3f)
                                )
                            ),
                            topLeft = Offset(x + 1f, y + 1f),
                            size = Size(barWidth - 2f, Math.min(8.dp.toPx(), barHeight / 3f)),
                            cornerRadius = cornerRadius
                        )
                    }
                }

                LiquidVisualizerMode.GLOW -> {
                    val centerX = width / 2f
                    val centerY = height / 2f
                    val baseRadius = if (isPlaying) {
                        (sin(phase) + 1.2f) * (height * 0.22f) + 24.dp.toPx()
                    } else {
                        24.dp.toPx()
                    }

                    // Outer Liquid Glass Aura Ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.6f),
                                accentColor.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = Offset(centerX, centerY),
                            radius = baseRadius * 1.8f
                        ),
                        center = Offset(centerX, centerY),
                        radius = baseRadius * 1.8f
                    )

                    // Inner Liquid Nucleus
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                accentColor,
                                MaterialTheme.colorScheme.tertiary
                            ),
                            center = Offset(centerX - 4f, centerY - 4f),
                            radius = baseRadius
                        ),
                        center = Offset(centerX, centerY),
                        radius = baseRadius
                    )

                    // Glass Highlight Reflection Rim
                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f),
                        center = Offset(centerX - baseRadius * 0.25f, centerY - baseRadius * 0.25f),
                        radius = baseRadius * 0.35f
                    )
                }
            }
        }
    }
}

/**
 * Liquid Glass Pill Mode Selector Bar for switching visualizer modes.
 */
@Composable
fun LiquidGlassVisualizerPillBar(
    currentMode: LiquidVisualizerMode,
    onModeSelect: (LiquidVisualizerMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = CircleShape,
                backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                borderColor = Color.White.copy(alpha = 0.25f),
                elevation = 6.dp
            )
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (m in LiquidVisualizerMode.entries) {
            LiquidGlassPill(
                text = when (m) {
                    LiquidVisualizerMode.WAVE -> "Liquid Wave"
                    LiquidVisualizerMode.BARS -> "Glass Bars"
                    LiquidVisualizerMode.GLOW -> "Aura Glow"
                },
                onClick = { onModeSelect(m) },
                isActive = currentMode == m,
                activeColor = MaterialTheme.colorScheme.primary,
                onActiveColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
