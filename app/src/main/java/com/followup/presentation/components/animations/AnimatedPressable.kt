package com.followup.presentation.components.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitPointerEventScope
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import com.followup.presentation.theme.ScaleValues
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * PREMIUM PRESSABLE CONTAINER
 * Provides tactile press feedback with scale + haptic
 */
@Composable
fun AnimatedPressable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedback: HapticFeedbackType = HapticFeedbackType.TextHandleMove,
    scalePressed: Float = ScaleValues.PRESSED,
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = scalePressed,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    haptic.performHapticFeedback(hapticFeedback)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Enhanced press modifier with immediate visual feedback
 * Uses pointerInput for zero-latency response
 */
fun Modifier.pressableScale(
    scalePressed: Float = ScaleValues.PRESSED,
    onPress: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null
): Modifier = composed {
    val scale = remember { Animatable(1f) }

    pointerInput(scalePressed) {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown()

                // Immediate press feedback
                scale.snapTo(scalePressed)
                onPress?.invoke()

                // Wait for release
                val up = waitForUpOrCancellation()

                // Release animation
                if (up != null) {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    onRelease?.invoke()
                } else {
                    // Cancelled - reset immediately
                    scale.snapTo(1f)
                }
            }
        }
    }.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/**
 * Ripple press effect with background color transition
 */
@Composable
fun AnimatedPressableWithRipple(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    pressedColor: Color = Color.Transparent.copy(alpha = 0.1f),
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            launch {
                scale.animateTo(
                    targetValue = ScaleValues.PRESSED,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
        } else {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        contentAlignment = Alignment.Center
    ) {
        // Ripple background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = pressedColor.copy(alpha = alpha.value * pressedColor.alpha)
                )
        )
        content()
    }
}

/**
 * PREMIUM ENHANCED RIPPLE PRESSABLE
 * Combines scale feedback with expanding radial ripple effect
 * Creates a satisfying "water drop" interaction feel
 */
@Composable
fun EnhancedRipplePressable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rippleColor: Color = Color.White.copy(alpha = 0.25f),
    hapticFeedback: HapticFeedbackType = HapticFeedbackType.TextHandleMove,
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Scale animation
    val scale = remember { Animatable(1f) }
    // Ripple expansion
    val rippleScale = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            launch {
                scale.animateTo(
                    targetValue = ScaleValues.PRESSED,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
            // Expanding ripple
            launch {
                rippleScale.snapTo(0f)
                rippleAlpha.snapTo(1f)
                rippleScale.animateTo(
                    targetValue = 2.5f,
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
            launch {
                rippleAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = 100,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
        } else {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                haptic.performHapticFeedback(hapticFeedback)
                onClick()
            }
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        contentAlignment = Alignment.Center
    ) {
        // Expanding ripple overlay
        if (rippleAlpha.value > 0.01f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleX = rippleScale.value
                        scaleY = rippleScale.value
                        alpha = rippleAlpha.value
                    }
                    .background(
                        color = rippleColor,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
        content()
    }
}
