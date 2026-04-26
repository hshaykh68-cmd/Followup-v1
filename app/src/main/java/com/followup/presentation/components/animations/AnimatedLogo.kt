package com.followup.presentation.components.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.followup.presentation.theme.FollowUpTheme

/**
 * PREMIUM ANIMATED LOGO
 * 
 * Minimal, high-contrast logo representing "FollowUp" concept.
 * - Stylized "F" with forward motion indicator
 * - Clean geometric construction
 * - Adaptive for light/dark themes
 */

@Composable
fun AnimatedLogo(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Custom vector logo drawn with Compose Canvas
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawPremiumLogo(color)
        }
    }
}

/**
 * Draw the premium logo with exact proportions.
 * Based on a 200x200 coordinate system, scaled to fit canvas.
 */
private fun DrawScope.drawPremiumLogo(color: Color) {
    val canvasSize = size.minDimension
    val scale = canvasSize / 200f
    
    scale(scale, scale) {
        translate(
            left = (size.width / scale - 200f) / 2,
            top = (size.height / scale - 200f) / 2
        ) {
            // Main "F" structure
            val mainPath = Path().apply {
                // Start at top-left of vertical stroke
                moveTo(60f, 40f)
                
                // Down vertical stroke
                lineTo(60f, 160f)
                
                // Right to end of bottom stroke
                lineTo(85f, 160f)
                
                // Up to start of middle stroke
                lineTo(85f, 105f)
                
                // Right across middle stroke
                lineTo(145f, 105f)
                
                // Up to start of top stroke
                lineTo(145f, 85f)
                
                // Left back to vertical
                lineTo(85f, 85f)
                
                // Up to top of vertical
                lineTo(85f, 60f)
                
                // Right across top
                lineTo(155f, 60f)
                
                // Down to close at start
                lineTo(155f, 40f)
                
                close()
            }
            
            // Draw main body
            drawPath(
                path = mainPath,
                color = color,
                style = Fill
            )
            
            // Forward indicator dot (accent)
            // Positioned to suggest forward motion
            val dotCenterX = 135f
            val dotCenterY = 125f
            val dotRadius = 14f
            
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(dotCenterX, dotCenterY)
            )
        }
    }
}

/**
 * Alternative: Simple icon-based logo for lighter builds
 */
@Composable
fun SimpleLogo(
    color: Color,
    modifier: Modifier = Modifier
) {
    // Fallback using Material icon with custom tint
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "FollowUp",
        tint = color,
        modifier = modifier
    )
}

// ============================================
// PREVIEWS
// ============================================

@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA)
@Composable
private fun AnimatedLogoLightPreview() {
    FollowUpTheme {
        AnimatedLogo(
            color = Color(0xFF18181B),
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF09090B)
@Composable
private fun AnimatedLogoDarkPreview() {
    FollowUpTheme(darkTheme = true) {
        AnimatedLogo(
            color = Color(0xFFFAFAFA),
            modifier = Modifier.size(120.dp)
        )
    }
}
