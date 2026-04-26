package com.followup.presentation.components.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.followup.presentation.theme.AnimationDuration
import com.followup.presentation.theme.OffsetValues
import com.followup.presentation.theme.Stagger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PREMIUM ANIMATED LIST ITEM
 * 
 * Provides smooth enter animation with stagger support
 * and graceful exit when removed.
 */
@Composable
fun <T> AnimatedListItem(
    item: T,
    index: Int,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onAnimationComplete: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    val density = LocalDensity.current
    val slideDistancePx = remember { with(density) { OffsetValues.SLIDE_STANDARD.dp.toPx() } }
    
    // Animation states
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.9f) }
    val offsetY = remember { Animatable(slideDistancePx) }
    var hasAnimatedIn by remember { mutableStateOf(false) }

    // Staggered entrance animation
    LaunchedEffect(item, isVisible) {
        if (isVisible && !hasAnimatedIn) {
            // Stagger delay based on index (capped)
            val staggerDelay = (index * Stagger.LIST_ITEM_DELAY).coerceAtMost(Stagger.MAX_STAGGER_TIME)
            delay(staggerDelay)
            
            // Launch all animations together
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.EMPHASIZED,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            hasAnimatedIn = true
            onAnimationComplete()
        }
    }

    // Exit animation handling
    LaunchedEffect(isVisible, hasAnimatedIn) {
        if (!isVisible && hasAnimatedIn) {
            launch {
                val result = alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.STANDARD,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 0.95f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.STANDARD,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = -slideDistancePx * 0.5f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.STANDARD,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .scale(scale.value)
            .graphicsLayer {
                translationY = offsetY.value
            }
    ) {
        content(item)
    }
}

/**
 * Animated list item with swipe-to-dismiss support
 */
@Composable
fun <T> AnimatedSwipeableItem(
    item: T,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val visibilityState = remember { MutableTransitionState(!isVisible) }
    visibilityState.targetState = isVisible

    AnimatedVisibility(
        visibleState = visibilityState,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialOffsetY = { it / 3 }
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = AnimationDuration.STANDARD,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        ) + slideOutVertically(
            animationSpec = tween(
                durationMillis = AnimationDuration.STANDARD,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            targetOffsetY = { -it / 4 }
        )
    ) {
        content()
    }
}

/**
 * Staggered list animation helper
 * Calculates the actual delay based on index and total items
 */
object ListAnimationHelper {
    fun calculateStaggerDelay(
        index: Int,
        totalItems: Int,
        baseDelay: Long = Stagger.LIST_ITEM_DELAY,
        maxStagger: Long = Stagger.MAX_STAGGER_TIME
    ): Long {
        val rawDelay = index * baseDelay
        return if (totalItems > 0) {
            // Adjust delay so last item doesn't exceed max stagger
            val adjustedDelay = rawDelay.coerceAtMost(maxStagger)
            // Distribute delays more evenly for large lists
            if (totalItems > 6) {
                (adjustedDelay * (6f / totalItems.coerceAtLeast(1))).toLong()
            } else {
                adjustedDelay
            }
        } else {
            rawDelay.coerceAtMost(maxStagger)
        }
    }
}

/**
 * Animated appearance for empty states
 */
@Composable
fun AnimatedEmptyState(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.95f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(100) // Small delay for natural feel
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.SMOOTH,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        } else {
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.STANDARD,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .scale(scale.value)
    ) {
        content()
    }
}

/**
 * PREMIUM EXIT ANIMATION WRAPPER
 * Smoothly animates items out when deleted/done with customizable exit direction
 */
@Composable
fun <T> AnimatedExitWrapper(
    item: T,
    isVisible: Boolean,
    onExitComplete: () -> Unit = {},
    exitDirection: ExitDirection = ExitDirection.SlideRight,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    val density = LocalDensity.current
    val slideDistancePx = remember { with(density) { 100.dp.toPx() } }
    
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    
    var hasExited by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (!isVisible && !hasExited) {
            // Exit animation
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.STANDARD,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 0.92f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.STANDARD,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            
            // Directional exit slide
            launch {
                val (targetX, targetY) = when (exitDirection) {
                    ExitDirection.SlideRight -> slideDistancePx to 0f
                    ExitDirection.SlideLeft -> -slideDistancePx to 0f
                    ExitDirection.SlideUp -> 0f to -slideDistancePx
                    ExitDirection.SlideDown -> 0f to slideDistancePx
                    ExitDirection.FadeOnly -> 0f to 0f
                }
                offsetX.animateTo(
                    targetValue = targetX,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                offsetY.animateTo(
                    targetValue = targetY,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            hasExited = true
            onExitComplete()
        } else if (isVisible) {
            // Reset when visible again
            alpha.snapTo(1f)
            scale.snapTo(1f)
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            hasExited = false
        }
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .scale(scale.value)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
            }
    ) {
        content(item)
    }
}

enum class ExitDirection {
    SlideRight,
    SlideLeft, 
    SlideUp,
    SlideDown,
    FadeOnly
}
