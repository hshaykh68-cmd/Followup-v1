package com.followup.presentation.components.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Elevation
import com.followup.presentation.theme.ScaleValues
import com.followup.presentation.theme.Spacing

/**
 * PREMIUM ANIMATED FAB
 * 
 * Features:
 * - Elegant entrance animation
 * - Press scale feedback
 * - Elevation change on press
 * - Optional pulse animation for attention
 */
@Composable
fun AnimatedExtendedFAB(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    enabled: Boolean = true,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimary,
    shouldPulse: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Entrance animation
    val entranceScale = remember { Animatable(0.8f) }
    val entranceAlpha = remember { Animatable(0f) }
    
    // Press animation
    val pressScale = remember { Animatable(1f) }
    val elevation = remember { Animatable(Elevation.high.value) }
    
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Entrance
    LaunchedEffect(Unit) {
        launch {
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            entranceAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
        }
    }

    // Press feedback
    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            launch {
                pressScale.animateTo(
                    targetValue = ScaleValues.EMPHASIZED_PRESSED,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
            launch {
                elevation.animateTo(
                    targetValue = Elevation.raised.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
        } else {
            launch {
                pressScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                elevation.animateTo(
                    targetValue = Elevation.high.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }

    val finalScale = if (shouldPulse && !isPressed) pulseScale else 1f
    val combinedScale = entranceScale.value * pressScale.value * finalScale

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .padding(end = Spacing.xs, bottom = Spacing.md)
            .graphicsLayer {
                scaleX = combinedScale
                scaleY = combinedScale
                alpha = entranceAlpha.value
                shadowElevation = elevation.value
            }
            .shadow(
                elevation = elevation.value.dp,
                shape = RoundedCornerShape(CornerRadius.xl),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(CornerRadius.xl)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

/**
 * Simple circular FAB with animations
 */
@Composable
fun AnimatedFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    enabled: Boolean = true,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimary,
    shouldPulse: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val entranceScale = remember { Animatable(0f) }
    val pressScale = remember { Animatable(1f) }
    val elevation = remember { Animatable(Elevation.high.value) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Entrance animation - pop in
    LaunchedEffect(Unit) {
        entranceScale.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 400
                0f at 0
                1.1f at 250
                1f at 400
            }
        )
    }

    // Press feedback
    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            launch {
                pressScale.animateTo(
                    targetValue = ScaleValues.EMPHASIZED_PRESSED,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
            launch {
                elevation.animateTo(
                    targetValue = Elevation.raised.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
        } else {
            launch {
                pressScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                elevation.animateTo(
                    targetValue = Elevation.high.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }

    val finalScale = if (shouldPulse && !isPressed) pulseScale else 1f
    val combinedScale = entranceScale.value * pressScale.value * finalScale

    Box(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = combinedScale
                scaleY = combinedScale
                shadowElevation = elevation.value
            }
            .shadow(
                elevation = elevation.value.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = containerColor.copy(alpha = 0.2f),
                spotColor = containerColor.copy(alpha = 0.3f)
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
    }
}
