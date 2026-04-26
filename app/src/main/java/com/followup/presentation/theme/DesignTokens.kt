package com.followup.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * FollowUp Design System Tokens
 * 
 * A comprehensive design system for a premium, modern 2026 feel.
 * Consistency > Decoration - minimal but refined.
 */

// ============================================================================
// SPACING SCALE
// ============================================================================

object Spacing {
    /** 4dp - Micro spacing for tight gaps */
    val xxs = 4.dp
    
    /** 8dp - Extra small spacing for compact elements */
    val xs = 8.dp
    
    /** 12dp - Small spacing for related elements */
    val sm = 12.dp
    
    /** 16dp - Default/base spacing */
    val md = 16.dp
    
    /** 20dp - Medium-large spacing for section separations */
    val ml = 20.dp
    
    /** 24dp - Large spacing for major sections */
    val lg = 24.dp
    
    /** 32dp - Extra large spacing for screen padding */
    val xl = 32.dp
    
    /** 40dp - 2x Extra large for major divisions */
    val xxl = 40.dp
    
    /** 48dp - 3x Extra large for hero sections */
    val xxxl = 48.dp
}

// ============================================================================
// CORNER RADIUS / SHAPE SCALE
// ============================================================================

object CornerRadius {
    /** 4dp - Subtle rounding for small elements like chips */
    val xs = 4.dp
    
    /** 8dp - Small rounding for input fields */
    val sm = 8.dp
    
    /** 12dp - Medium rounding for buttons, small cards */
    val md = 12.dp
    
    /** 16dp - Standard rounding for cards */
    val lg = 16.dp
    
    /** 20dp - Large rounding for prominent cards */
    val xl = 20.dp
    
    /** 24dp - Extra large rounding for hero cards */
    val xxl = 24.dp
    
    /** 28dp - Maximum rounding for bottom sheets */
    val xxxl = 28.dp
    
    /** 32dp - Full rounding for FABs, large containers */
    val full = 32.dp
}

// ============================================================================
// ELEVATION SCALE
// ============================================================================

object Elevation {
    /** 0dp - Flat, no elevation */
    val none = 0.dp
    
    /** 1dp - Subtle elevation for resting cards */
    val subtle = 1.dp
    
    /** 2dp - Low elevation for pressed states, tonal surfaces */
    val low = 2.dp
    
    /** 3dp - Medium-low elevation for navigation */
    val nav = 3.dp
    
    /** 4dp - Medium elevation for raised cards */
    val medium = 4.dp
    
    /** 6dp - Medium-high for bottom sheets */
    val raised = 6.dp
    
    /** 8dp - High elevation for FABs, dialogs */
    val high = 8.dp
}

// ============================================================================
// COMPONENT-SPECIFIC TOKENS
// ============================================================================

object ComponentTokens {
    // Cards
    object Card {
        val radius = CornerRadius.xxl
        val paddingHorizontal = Spacing.lg
        val paddingVertical = Spacing.ml
        val elevationDefault = Elevation.subtle
        val elevationPressed = Elevation.medium
    }
    
    // Buttons
    object Button {
        val radius = CornerRadius.lg
        val paddingHorizontal = Spacing.lg
        val paddingVertical = Spacing.sm
        val heightMin = 48.dp
    }
    
    // Input Fields
    object Input {
        val radius = CornerRadius.lg
        val heightMin = 56.dp
    }
    
    // Chips
    object Chip {
        val radius = CornerRadius.md
        val heightMin = 32.dp
    }
    
    // Icons
    object Icon {
        val sizeXs = 12.dp
        val sizeSm = 16.dp
        val sizeMd = 20.dp
        val sizeLg = 24.dp
        val sizeXl = 32.dp
        val sizeXxl = 48.dp
    }
    
    // Status Indicator
    object Status {
        val size = 10.dp
    }
    
    // Bottom Sheet
    object BottomSheet {
        val radius = CornerRadius.xxxl
        val padding = Spacing.lg
        val dragHandleWidth = 40.dp
        val dragHandleHeight = 4.dp
    }
    
    // Navigation
    object Nav {
        val elevation = Elevation.nav
    }
}

// ============================================================================
// SCREEN LAYOUT TOKENS
// ============================================================================

object Screen {
    /** Standard screen horizontal padding */
    val paddingHorizontal = Spacing.lg
    
    /** Standard screen vertical padding */
    val paddingVertical = Spacing.md
    
    /** Section spacing between major content blocks */
    val sectionSpacing = Spacing.lg
    
    /** Item spacing within lists */
    val itemSpacing = Spacing.xs
}

// ============================================================================
// CONTENT TOKENS
// ============================================================================

object Content {
    /** Max lines for card titles */
    val titleMaxLines = 1
    
    /** Max lines for card body text */
    val bodyMaxLines = 2
    
    /** Alpha for secondary/disabled text */
    val alphaSecondary = 0.7f
    
    /** Alpha for disabled/complete items */
    val alphaDisabled = 0.5f
    
    /** Alpha for subtle/hint text */
    val alphaHint = 0.45f
}
