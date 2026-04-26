package com.followup.presentation.components.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.followup.presentation.theme.AnimationDuration
import com.followup.presentation.theme.Stagger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PREMIUM BOTTOM SHEET CONTENT
 * Progressive staggered entrance animation for natural appearance
 */
@Composable
fun AnimatedBottomSheetContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        content()
    }
}

/**
 * Individual animated bottom sheet section with entrance animation
 */
@Composable
fun AnimatedSheetSection(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val slideDistancePx = remember { with(density) { 30.dp.toPx() } }
    
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.95f) }
    val offsetY = remember { Animatable(slideDistancePx) }

    LaunchedEffect(Unit) {
        // Staggered entrance
        val staggerDelay = (index * Stagger.LIST_ITEM_DELAY).coerceAtMost(200)
        delay(staggerDelay)
        
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
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .scale(scale.value)
            .graphicsLayer {
                translationY = offsetY.value
            }
    ) {
        content()
    }
}

/**
 * Animated sheet item with horizontal slide entrance
 * Used for buttons, chips, and other interactive elements
 */
@Composable
fun AnimatedSheetItem(
    index: Int,
    modifier: Modifier = Modifier,
    slideFrom: SlideFrom = SlideFrom.Bottom,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val slideDistancePx = remember { with(density) { 20.dp.toPx() } }
    
    val alpha = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val staggerDelay = (index * 40L).coerceAtMost(150)
        delay(staggerDelay)
        
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = AnimationDuration.STANDARD,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                )
            )
        }
        
        val (targetX, targetY) = when (slideFrom) {
            SlideFrom.Left -> -slideDistancePx to 0f
            SlideFrom.Right -> slideDistancePx to 0f
            SlideFrom.Bottom -> 0f to slideDistancePx
            SlideFrom.Top -> 0f to -slideDistancePx
        }
        
        offsetX.snapTo(targetX)
        offsetY.snapTo(targetY)
        
        launch {
            offsetX.animateTo(
                targetValue = 0f,
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
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
            }
    ) {
        content()
    }
}

enum class SlideFrom {
    Left,
    Right,
    Bottom,
    Top
}

/**
 * Animated sheet divider with subtle entrance
 */
@Composable
fun AnimatedSheetDivider(
    index: Int,
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay((index * 30L).coerceAtMost(200))
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = AnimationDuration.STANDARD,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }
    
    Spacer(
        modifier = modifier
            .alpha(alpha.value)
            .height(1.dp)
    )
}
