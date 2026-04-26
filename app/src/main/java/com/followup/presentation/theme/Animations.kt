package com.followup.presentation.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * PREMIUM ANIMATION SPECIFICATIONS - 2026 Quality
 * 
 * These animation specs are designed for:
 * - 60fps smooth performance
 * - Natural, physics-based motion
 * - Subtle, premium feel (not flashy)
 * - Consistent timing across the app
 */

// ============================================
// EASING CURVES - Premium natural motion
// ============================================

/** Standard easing for UI transitions - smooth acceleration/deceleration */
val StandardEasing: Easing = FastOutSlowInEasing

/** Expressive easing for emphasized motion - slightly more dramatic */
val ExpressiveEasing: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

/** Quick easing for responsive interactions - fast start, smooth end */
val QuickEasing: Easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)

/** Soft easing for gentle appearance - slow and subtle */
val SoftEasing: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

/** Decelerate easing for enter animations - fast start, slow end */
val DecelerateEasing: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

/** Accelerate easing for exit animations - slow start, fast end */
val AccelerateEasing: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

// ============================================
// DURATION CONSTANTS - Consistent timing
// ============================================

object AnimationDuration {
    /** Instant feedback - 50ms */
    const val INSTANT = 50
    
    /** Quick feedback - 100ms (button presses) */
    const val QUICK = 100
    
    /** Standard transition - 200ms (state changes) */
    const val STANDARD = 200
    
    /** Emphasized transition - 300ms (entrances) */
    const val EMPHASIZED = 300
    
    /** Smooth transition - 400ms (complex animations) */
    const val SMOOTH = 400
    
    /** Slow/ambient - 500ms (hero elements) */
    const val SLOW = 500
}

// ============================================
// SPRING SPECS - Physics-based natural motion
// ============================================

object SpringSpecs {
    /**
     * Premium spring for list item animations
     * Slight bounce for satisfying feel
     */
    val ListItemEnter = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    /**
     * Quick spring for responsive interactions
     * Fast return for snappy feel
     */
    val QuickResponse = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
    
    /**
     * Soft spring for gentle animations
     * No bounce, very smooth
     */
    val Soft = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    /**
     * Expressive spring for emphasized elements
     * Noticeable bounce for delight
     */
    val Expressive = spring<Float>(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    /**
     * FAB press spring
     * Immediate response with subtle bounce
     */
    val FabPress = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    /**
     * Swipe gesture spring
     * Natural resistance and return
     */
    val Swipe = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}

// ============================================
// TWEEN SPECS - Precise timing animations
// ============================================

object TweenSpecs {
    /** Standard fade animation */
    fun <T> fade(duration: Int = AnimationDuration.STANDARD) = tween<T>(
        durationMillis = duration,
        easing = StandardEasing
    )
    
    /** Quick fade for micro-interactions */
    fun <T> quickFade() = tween<T>(
        durationMillis = AnimationDuration.QUICK,
        easing = QuickEasing
    )
    
    /** Soft fade for gentle appearances */
    fun <T> softFade(duration: Int = AnimationDuration.EMPHASIZED) = tween<T>(
        durationMillis = duration,
        easing = SoftEasing
    )
    
    /** Enter slide with deceleration */
    fun <T> slideEnter(duration: Int = AnimationDuration.EMPHASIZED) = tween<T>(
        durationMillis = duration,
        easing = DecelerateEasing
    )
    
    /** Exit slide with acceleration */
    fun <T> slideExit(duration: Int = AnimationDuration.STANDARD) = tween<T>(
        durationMillis = duration,
        easing = AccelerateEasing
    )
}

// ============================================
// SCALE VALUES - Press interactions
// ============================================

object ScaleValues {
    /** Scale when pressing a button/card */
    const val PRESSED = 0.97f
    
    /** Scale for hover/highlight state */
    const val HOVERED = 0.99f
    
    /** Scale for emphasized press (FABs) */
    const val EMPHASIZED_PRESSED = 0.95f
    
    /** Normal/resting scale */
    const val NORMAL = 1.0f
    
    /** Scale for subtle emphasis (chips) */
    const val SUBTLE_EMPHASIS = 1.02f
}

// ============================================
// ALPHA VALUES - Opacity transitions
// ============================================

object AlphaValues {
    /** Fully visible */
    const val VISIBLE = 1f
    
    /** Slightly dimmed (disabled/hint) */
    const val DIMMED = 0.6f
    
    /** Very subtle (background elements) */
    const val SUBTLE = 0.3f
    
    /** Nearly invisible (exit animations) */
    const val GONE = 0f
}

// ============================================
// OFFSET VALUES - Slide distances
// ============================================

object OffsetValues {
    /** Small slide for subtle enter (dp) */
    const val SLIDE_SMALL = 20
    
    /** Standard slide for list items (dp) */
    const val SLIDE_STANDARD = 40
    
    /** Large slide for dramatic entrances (dp) */
    const val SLIDE_LARGE = 80
    
    /** Exit slide distance (dp) */
    const val SLIDE_EXIT = 100
}

// ============================================
// STAGGER TIMING - Sequential animations
// ============================================

object Stagger {
    /** Delay between list items appearing */
    const val LIST_ITEM_DELAY = 50L
    
    /** Delay for staggered grid items */
    const val GRID_ITEM_DELAY = 30L
    
    /** Maximum total stagger time */
    const val MAX_STAGGER_TIME = 300L
}
