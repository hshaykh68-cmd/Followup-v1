package com.followup.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.followup.presentation.components.animations.AnimatedLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PREMIUM SPLASH SCREEN - 2026 Quality Animation
 * 
 * Animation Sequence (Total: ~1000ms):
 * 1. Initial state (0ms): Background visible, logo at 95% scale, opacity 0.6
 * 2. Main animation (0-600ms): Opacity → 1.0, scale → 1.0 with smooth ease-out
 * 3. Polish phase (600-1000ms): Subtle elevation feel, micro movement
 * 4. Seamless handoff to main content
 * 
 * Performance: 60fps, no UI thread blocking, hardware accelerated
 */

// ============================================
// PREMIUM EASING CURVES
// ============================================

/** Smooth ease-out for main entrance - decelerates naturally */
private val PremiumEaseOut: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

/** Ultra-smooth for polish phase - barely perceptible */
private val MicroEasing: Easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)

// ============================================
// ANIMATION CONSTANTS
// ============================================

object SplashAnimationSpec {
    /** Main entrance duration - 600ms */
    const val MAIN_DURATION = 600
    
    /** Polish phase duration - 400ms */
    const val POLISH_DURATION = 400
    
    /** Total animation time - 1000ms */
    const val TOTAL_DURATION = 1000
    
    /** Initial scale - 95% */
    const val INITIAL_SCALE = 0.95f
    
    /** Final scale - 100% */
    const val FINAL_SCALE = 1.0f
    
    /** Initial opacity - 60% */
    const val INITIAL_ALPHA = 0.6f
    
    /** Final opacity - 100% */
    const val FINAL_ALPHA = 1.0f
    
    /** Micro float amplitude - 2dp */
    const val MICRO_FLOAT = 2f
    
    /** Elevation shadow during polish - 8dp */
    const val POLISH_ELEVATION = 8
}

// ============================================
// COMPOSE SPLASH CONTENT
// ============================================

/**
 * Premium animated splash content with exact timing specification.
 * 
 * @param onAnimationComplete - Called when 1000ms animation sequence finishes
 * @param modifier - Optional modifier for container
 */
@Composable
fun AnimatedSplashContent(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF09090B) else Color(0xFFFAFAFA)
    val contentColor = if (isDark) Color(0xFFFAFAFA) else Color(0xFF18181B)
    
    // Animation states
    val scale = remember { Animatable(SplashAnimationSpec.INITIAL_SCALE) }
    val alpha = remember { Animatable(SplashAnimationSpec.INITIAL_ALPHA) }
    val offsetY = remember { Animatable(0f) }
    val elevation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        // Phase 1: Main animation (0-600ms)
        // Simultaneous scale and opacity with smooth ease-out
        launch {
            scale.animateTo(
                targetValue = SplashAnimationSpec.FINAL_SCALE,
                animationSpec = tween(
                    durationMillis = SplashAnimationSpec.MAIN_DURATION,
                    easing = PremiumEaseOut
                )
            )
        }
        
        launch {
            alpha.animateTo(
                targetValue = SplashAnimationSpec.FINAL_ALPHA,
                animationSpec = tween(
                    durationMillis = SplashAnimationSpec.MAIN_DURATION,
                    easing = PremiumEaseOut
                )
            )
        }
        
        // Wait for main animation
        delay(SplashAnimationSpec.MAIN_DURATION.toLong())
        
        // Phase 2: Polish phase (600-1000ms)
        // Subtle micro-float and elevation
        launch {
            offsetY.animateTo(
                targetValue = -SplashAnimationSpec.MICRO_FLOAT,
                animationSpec = tween(
                    durationMillis = SplashAnimationSpec.POLISH_DURATION,
                    easing = MicroEasing
                )
            )
        }
        
        launch {
            elevation.animateTo(
                targetValue = SplashAnimationSpec.POLISH_ELEVATION.toFloat(),
                animationSpec = tween(
                    durationMillis = SplashAnimationSpec.POLISH_DURATION,
                    easing = MicroEasing
                )
            )
        }
        
        // Complete polish phase
        delay(SplashAnimationSpec.POLISH_DURATION.toLong())
        
        // Notify completion
        onAnimationComplete()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Animated Logo
        Box(
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .offset(y = offsetY.value.dp)
                .shadow(
                    elevation = elevation.value.dp,
                    shape = CircleShape,
                    ambientColor = contentColor.copy(alpha = 0.1f),
                    spotColor = contentColor.copy(alpha = 0.05f)
                )
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedLogo(
                color = contentColor,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

