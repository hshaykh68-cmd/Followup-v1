package com.followup.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * FollowUp Typography System
 * 
 * Premium typography with sharp hierarchy for maximum readability.
 * 
 * HIERARCHY (from most to least prominent):
 * 
 * 1. DISPLAY (48-57sp) - Hero stats, big numbers
 *    - displayLarge: 57sp/64sp - Largest numbers
 *    - displayMedium: 48sp/56sp - Featured stats  
 *    - displaySmall: 36sp/44sp - Card hero numbers
 * 
 * 2. HEADLINE (24-32sp) - Screen titles, major headers
 *    - headlineLarge: 32sp/40sp - App title (premium weight)
 *    - headlineMedium: 28sp/36sp - Screen titles
 *    - headlineSmall: 24sp/32sp - Sheet/modal titles
 * 
 * 3. TITLE (16-22sp) - Card titles, section headers
 *    - titleLarge: 22sp/28sp - Major card titles
 *    - titleMedium: 18sp/26sp - Card names (primary focus)
 *    - titleSmall: 14sp/20sp - Section labels
 * 
 * 4. BODY (14-16sp) - Content text
 *    - bodyLarge: 16sp/24sp - Primary descriptions
 *    - bodyMedium: 14sp/20sp - Secondary content, card messages
 *    - bodySmall: 12sp/16sp - Tertiary info
 * 
 * 5. LABEL (11-14sp) - Buttons, captions, metadata
 *    - labelLarge: 14sp/20sp - Buttons, chips
 *    - labelMedium: 12sp/16sp - Time chips, badges
 *    - labelSmall: 11sp/16sp - Captions, hints
 */

private val defaultFontFamily = FontFamily.Default

// ============================================================================
// DISPLAY - Hero numbers, stats (Maximum visual weight)
// ============================================================================

private val displayLarge = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 57.sp,
    lineHeight = 64.sp,
    letterSpacing = (-0.25).sp
)

private val displayMedium = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-0.25).sp
)

private val displaySmall = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.15).sp
)

// ============================================================================
// HEADLINE - Screen titles, major headers (Strong presence)
// ============================================================================

private val headlineLarge = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Bold,           // Bolder for app title presence
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp                // Tighter for premium feel
)

private val headlineMedium = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.25).sp
)

private val headlineSmall = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.15).sp
)

// ============================================================================
// TITLE - Card titles, list headers (Clear distinction from body)
// ============================================================================

private val titleLarge = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

private val titleMedium = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,                       // Larger for card name prominence
    lineHeight = 26.sp,                     // Better breathing room
    letterSpacing = 0.sp
)

private val titleSmall = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.SemiBold,       // Stronger for section headers
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

// ============================================================================
// BODY - Content text, descriptions (Readable, comfortable)
// ============================================================================

private val bodyLarge = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.25.sp                 // Slightly reduced
)

private val bodyMedium = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,                      // Tight but readable
    letterSpacing = 0.15.sp
)

private val bodySmall = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.25.sp
)

// ============================================================================
// LABEL - Buttons, captions, metadata (Compact but clear)
// ============================================================================

private val labelLarge = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

private val labelMedium = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.25.sp                   // Reduced from 0.5
)

private val labelSmall = TextStyle(
    fontFamily = defaultFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 14.sp,                       // Tighter for captions
    letterSpacing = 0.25.sp
)

// ============================================================================
// APP TYPOGRAPHY
// ============================================================================

val AppTypography = Typography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    displaySmall = displaySmall,
    headlineLarge = headlineLarge,
    headlineMedium = headlineMedium,
    headlineSmall = headlineSmall,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall
)
