package com.theveloper.pixelplay.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape

/**
 * Creates a translucent liquid glass background modifier with specular highlights,
 * inner specular edge glow, and glossy reflection gradients.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    borderColor: Color = Color.White.copy(alpha = 0.25f),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 8.dp
): Modifier {
    val glassGradient = remember(backgroundColor) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.15f),
                backgroundColor,
                Color.Black.copy(alpha = 0.25f)
            )
        )
    }

    val specularBorder = remember(borderColor) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                borderColor,
                Color.White.copy(alpha = 0.10f)
            )
        )
    }

    return this
        .shadow(elevation, shape, clip = false)
        .clip(shape)
        .background(glassGradient)
        .border(borderWidth, specularBorder, shape)
}

/**
 * Liquid Glass Surface container composable with smooth corner shapes and specular borders.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = AbsoluteSmoothCornerShape(cornerRadius = 24.dp, smoothnessAsPercent = 60),
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
    borderColor: Color = Color.White.copy(alpha = 0.25f),
    elevation: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.liquidGlass(
            shape = shape,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            elevation = elevation
        )
    ) {
        content()
    }
}

/**
 * Interactive Liquid Glass Pill chip/button with dynamic specular shine,
 * active highlight state, and smooth tactile spring compression.
 */
@Composable
fun LiquidGlassPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    onActiveColor: Color = MaterialTheme.colorScheme.onPrimary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    onInactiveColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else if (isActive) 1.02f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "pillScale"
    )

    val backgroundBrush = if (isActive) {
        Brush.linearGradient(
            colors = listOf(
                activeColor.copy(alpha = 0.9f),
                activeColor.copy(alpha = 0.65f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.15f),
                inactiveColor,
                Color.Black.copy(alpha = 0.2f)
            )
        )
    }

    val borderBrush = if (isActive) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.7f),
                activeColor.copy(alpha = 0.8f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.10f)
            )
        )
    }

    val contentColor = if (isActive) onActiveColor else onInactiveColor

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(backgroundBrush)
            .border(1.dp, borderBrush, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            icon()
        }
        Text(
            text = text,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
