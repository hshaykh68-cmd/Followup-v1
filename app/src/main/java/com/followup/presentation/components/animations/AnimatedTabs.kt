package com.followup.presentation.components.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.followup.presentation.theme.CornerRadius
import kotlinx.coroutines.launch
import com.followup.presentation.theme.ScaleValues
import com.followup.presentation.theme.Spacing

/**
 * PREMIUM ANIMATED TAB ROW
 * 
 * Features:
 * - Animated sliding indicator (pill)
 * - Press feedback on tabs
 * - Smooth content transitions
 * - Haptic feedback on selection
 */
@Composable
fun <T> AnimatedTabRow(
    tabs: List<Pair<T, String>>,
    selectedTab: T,
    onTabSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    selectedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    
    // Track tab positions for indicator
    val tabPositions = remember { mutableStateListOf<Dp>() }
    val tabWidths = remember { mutableStateListOf<Dp>() }
    
    // Indicator position animation
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }
    val indicatorOffset = remember { Animatable(0f) }
    val indicatorWidth = remember { Animatable(0f) }

    // Animate indicator to selected tab
    LaunchedEffect(selectedIndex, tabPositions, tabWidths) {
        if (selectedIndex >= 0 && selectedIndex < tabPositions.size) {
            val targetOffset = with(density) { tabPositions[selectedIndex].toPx() }
            val targetWidth = with(density) { tabWidths[selectedIndex].toPx() }
            
            launch {
                indicatorOffset.animateTo(
                    targetValue = targetOffset,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                indicatorWidth.animateTo(
                    targetValue = targetWidth,
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
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(CornerRadius.xl)
            )
            .padding(Spacing.xs)
    ) {
        // Animated selection pill (background)
        Box(
            modifier = Modifier
                .offset(x = with(density) { indicatorOffset.value.toDp() })
                .width(with(density) { indicatorWidth.value.toDp() })
                .height(40.dp)
                .background(
                    color = selectedColor,
                    shape = RoundedCornerShape(CornerRadius.lg)
                )
        )

        // Tab buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, (tab, label) ->
                val isSelected = selectedIndex == index
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale = remember { Animatable(1f) }

                // Press feedback
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        scale.animateTo(
                            targetValue = ScaleValues.PRESSED,
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
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale.value)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (tab != selectedTab) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(tab)
                            }
                        }
                        .onGloballyPositioned { layoutCoordinates ->
                            if (tabPositions.size > index) {
                                tabPositions[index] = with(density) { layoutCoordinates.positionInParent().x.toDp() }
                                tabWidths[index] = with(density) { layoutCoordinates.size.width.toDp() }
                            } else {
                                tabPositions.add(with(density) { layoutCoordinates.positionInParent().x.toDp() })
                                tabWidths.add(with(density) { layoutCoordinates.size.width.toDp() })
                            }
                        }
                        .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated text weight
                    val animatedWeight by androidx.compose.runtime.remember { 
                        androidx.compose.animation.core.Animatable(if (isSelected) 600f else 500f) 
                    }
                    
                    LaunchedEffect(isSelected) {
                        animatedWeight.animateTo(
                            targetValue = if (isSelected) 600f else 500f,
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                dampingRatio = Spring.DampingRatioNoBouncy
                            )
                        )
                    }

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight(animatedWeight.value.toInt()),
                        color = if (isSelected) selectedTextColor else unselectedTextColor
                    )
                }
            }
        }
    }
}

/**
 * Tab content with smooth transition
 */
@Composable
fun <T> AnimatedTabContent(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            // Determine direction based on tab order
            val targetIndex = (targetState as? Enum<*>)?.ordinal ?: 0
            val initialIndex = (initialState as? Enum<*>)?.ordinal ?: 0
            
            val slideDirection = if (targetIndex > initialIndex) 1 else -1
            
            (fadeIn(
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            ) + slideInHorizontally(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialOffsetX = { fullWidth -> fullWidth / 4 * slideDirection }
            )) togetherWith (
                fadeOut(
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                ) + slideOutHorizontally(
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    targetOffsetX = { fullWidth -> -fullWidth / 4 * slideDirection }
                )
            )
        },
        label = "tab_content"
    ) { state ->
        content(state)
    }
}

/**
 * Premium animated tab button
 */
@Composable
fun AnimatedTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = ScaleValues.PRESSED,
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
            .scale(scale.value)
            .alpha(alpha.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .background(
                color = if (isSelected) selectedColor else Color.Transparent,
                shape = RoundedCornerShape(CornerRadius.lg)
            )
            .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) selectedTextColor else unselectedTextColor
        )
    }
}
