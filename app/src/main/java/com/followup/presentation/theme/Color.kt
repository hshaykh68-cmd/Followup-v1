package com.followup.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * FollowUp Color System
 * 
 * A refined, cohesive color palette for a premium 2026 aesthetic.
 * All colors work harmoniously in both light and dark themes.
 */

// ============================================================================
// PRIMARY PALETTE - Refined purple/indigo that feels modern
// ============================================================================

val primaryLight = Color(0xFF6366F1)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFEEF2FF)
val onPrimaryContainerLight = Color(0xFF312E81)

val secondaryLight = Color(0xFF64748B)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFF1F5F9)
val onSecondaryContainerLight = Color(0xFF334155)

val tertiaryLight = Color(0xFF8B5CF6)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFF3E8FF)
val onTertiaryContainerLight = Color(0xFF581C87)

// ============================================================================
// BACKGROUND & SURFACE - Warm, refined neutrals
// ============================================================================

val backgroundLight = Color(0xFFFAFAFA)
val onBackgroundLight = Color(0xFF18181B)
val surfaceLight = Color(0xFFFFFFFF)
val onSurfaceLight = Color(0xFF18181B)
val surfaceVariantLight = Color(0xFFF4F4F5)
val onSurfaceVariantLight = Color(0xFF71717A)

// ============================================================================
// UTILITIES - Refined borders and overlays
// ============================================================================

val outlineLight = Color(0xFFA1A1AA)
val outlineVariantLight = Color(0xFFD4D4D8)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF27272A)
val inverseOnSurfaceLight = Color(0xFFF4F4F5)
val inversePrimaryLight = Color(0xFFA5B4FC)

// ============================================================================
// STATUS COLORS - Semantic, cohesive palette
// ============================================================================

val errorLight = Color(0xFFEF4444)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFEE2E2)
val onErrorContainerLight = Color(0xFF991B1B)

// Warning - Amber
val warningLight = Color(0xFFF59E0B)
val onWarningLight = Color(0xFFFFFFFF)
val warningContainerLight = Color(0xFFFEF3C7)
val onWarningContainerLight = Color(0xFF92400E)

// Success - Emerald
val successLight = Color(0xFF10B981)
val onSuccessLight = Color(0xFFFFFFFF)
val successContainerLight = Color(0xFFD1FAE5)
val onSuccessContainerLight = Color(0xFF065F46)

// Info - Blue
val infoLight = Color(0xFF3B82F6)
val onInfoLight = Color(0xFFFFFFFF)
val infoContainerLight = Color(0xFFDBEAFE)
val onInfoContainerLight = Color(0xFF1E40AF)

// ============================================================================
// DARK THEME
// ============================================================================

val primaryDark = Color(0xFFA5B4FC)
val onPrimaryDark = Color(0xFF312E81)
val primaryContainerDark = Color(0xFF4338CA)
val onPrimaryContainerDark = Color(0xFFEEF2FF)

val secondaryDark = Color(0xFF94A3B8)
val onSecondaryDark = Color(0xFF334155)
val secondaryContainerDark = Color(0xFF475569)
val onSecondaryContainerDark = Color(0xFFF1F5F9)

val tertiaryDark = Color(0xFFC4B5FD)
val onTertiaryDark = Color(0xFF581C87)
val tertiaryContainerDark = Color(0xFF7C3AED)
val onTertiaryContainerDark = Color(0xFFF3E8FF)

val backgroundDark = Color(0xFF09090B)
val onBackgroundDark = Color(0xFFFAFAFA)
val surfaceDark = Color(0xFF18181B)
val onSurfaceDark = Color(0xFFFAFAFA)
val surfaceVariantDark = Color(0xFF27272A)
val onSurfaceVariantDark = Color(0xFFA1A1AA)

val outlineDark = Color(0xFF52525B)
val outlineVariantDark = Color(0xFF3F3F46)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE4E4E7)
val inverseOnSurfaceDark = Color(0xFF27272A)
val inversePrimaryDark = Color(0xFF6366F1)

val errorDark = Color(0xFFFCA5A5)
val onErrorDark = Color(0xFF7F1D1D)
val errorContainerDark = Color(0xFFB91C1C)
val onErrorContainerDark = Color(0xFFFEE2E2)

val warningDark = Color(0xFFFCD34D)
val onWarningDark = Color(0xFF78350F)
val warningContainerDark = Color(0xFFB45309)
val onWarningContainerDark = Color(0xFFFEF3C7)

val successDark = Color(0xFF6EE7B7)
val onSuccessDark = Color(0xFF064E3B)
val successContainerDark = Color(0xFF059669)
val onSuccessContainerDark = Color(0xFFD1FAE5)

val infoDark = Color(0xFF93C5FD)
val onInfoDark = Color(0xFF1E3A8A)
val infoContainerDark = Color(0xFF2563EB)
val onInfoContainerDark = Color(0xFFDBEAFE)

// ============================================================================
// SEMANTIC APP COLORS - Unified status colors
// ============================================================================

/** Completed/Done - Success green */
val statusDone = successLight
val statusDoneDark = successDark

/** Pending/Scheduled - Info blue */
val statusPending = infoLight
val statusPendingDark = infoDark

/** Overdue - Error red */
val statusOverdue = errorLight
val statusOverdueDark = errorDark

/** Warning/Attention - Warning amber */
val statusWarning = warningLight
val statusWarningDark = warningDark

/** Streak/Achievement - Primary accent */
val statusAccent = primaryLight
val statusAccentDark = primaryDark

// ============================================================================
// EXTENDED SEMANTIC COLORS - For premium feel
// ============================================================================

/** Surface tint for tonal elevation */
val surfaceTintLight = primaryLight.copy(alpha = 0.05f)
val surfaceTintDark = primaryDark.copy(alpha = 0.08f)

/** Streak/Achievement - Amber gold for heat/streak indicators */
val streakGold = Color(0xFFF59E0B)
val streakGoldDark = Color(0xFFFCD34D)

/** Pending/Scheduled - Blue for pending items */
val pendingBlue = infoLight
val pendingBlueDark = infoDark

/** Done/Completed - Green for completed items */
val doneGreen = successLight
val doneGreenDark = successDark

/** Divider/separator colors */
val dividerLight = outlineVariantLight.copy(alpha = 0.5f)
val dividerDark = outlineVariantDark.copy(alpha = 0.5f)

/** Disabled state alpha */
const val disabledAlpha = 0.38f
const val disabledContainerAlpha = 0.12f
