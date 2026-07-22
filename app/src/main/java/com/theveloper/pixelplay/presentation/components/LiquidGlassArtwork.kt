package com.theveloper.pixelplay.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theveloper.pixelplay.R
import com.theveloper.pixelplay.data.model.Song
import kotlinx.coroutines.launch
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape

/**
 * Liquid Glass Artwork component for displaying album cover art inside an interactive
 * 3D liquid glass housing with specular lens reflections, ambient liquid halo glow,
 * and overlaid liquid pill badges.
 */
@Composable
fun LiquidGlassArtwork(
    song: Song?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    badgeText: String? = "Lossless 24-bit",
    shape: Shape = AbsoluteSmoothCornerShape(cornerRadius = 32.dp, smoothnessAsPercent = 60)
) {
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Ambient liquid halo pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "liquidHaloTransition")
    val haloPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (isPlaying) 1.12f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulseScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    rotationX = (pan.y / 20f).coerceIn(-15f, 15f)
                    rotationY = (-pan.x / 20f).coerceIn(-15f, 15f)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Ambient Liquid Halo Glow behind cover
        val accentGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = if (isPlaying) 0.55f else 0.25f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = haloPulseScale
                    scaleY = haloPulseScale
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentGlowColor,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(32.dp)
        )

        // 3D Liquid Glass Interactive Housing
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.rotationX = rotationX
                    this.rotationY = rotationY
                    cameraDistance = 12f * density
                }
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.35f)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.4f)
                        )
                    ),
                    shape = shape
                )
        ) {
            // Song Cover Image
            SmartImage(
                model = song?.albumArtUriString ?: R.drawable.rounded_album_24,
                shape = shape,
                contentDescription = song?.title ?: "Album Cover",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = if (isPlaying) 1.03f else 1.0f
                        scaleY = if (isPlaying) 1.03f else 1.0f
                    },
                contentScale = ContentScale.Crop
            )

            // Liquid Glass Specular Lens Reflection Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            // Specular Glass Inner Rim Border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = shape
                    )
            )

            // Overlaid Liquid Glass Pill Badge
            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    LiquidGlassPill(
                        text = badgeText,
                        onClick = {},
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.primaryContainer,
                        onActiveColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
