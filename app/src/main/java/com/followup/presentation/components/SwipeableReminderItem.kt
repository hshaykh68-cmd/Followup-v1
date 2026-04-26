package com.followup.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.followup.domain.model.Reminder
import com.followup.presentation.theme.doneGreen
import com.followup.presentation.theme.pendingBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableReminderItem(
    reminder: Reminder,
    onDone: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val currentOnDone by rememberUpdatedState(onDone)
    val currentOnSnooze by rememberUpdatedState(onSnooze)
    var hasTriggeredProgressHaptic by remember { mutableStateOf(false) }
    var hasTriggeredConfirmHaptic by remember { mutableStateOf(false) }

    // Spring-animated progress for smoother visual feedback
    val animatedProgress = remember { Animatable(0f) }
    val dismissDirection = remember { mutableStateOf(SwipeToDismissBoxValue.Settled) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    if (!hasTriggeredConfirmHaptic) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hasTriggeredConfirmHaptic = true
                    }
                    currentOnSnooze()
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    if (!hasTriggeredConfirmHaptic) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hasTriggeredConfirmHaptic = true
                    }
                    currentOnDone()
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.35f }
    )

    // Smooth progress animation with spring physics
    LaunchedEffect(dismissState.progress, dismissState.dismissDirection) {
        dismissDirection.value = dismissState.dismissDirection
        launch {
            animatedProgress.animateTo(
                targetValue = dismissState.progress.coerceIn(0f, 1f),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        }
    }

    // Progress-based haptic (light tick at 50%, stronger at 75%)
    LaunchedEffect(animatedProgress.value) {
        val progress = animatedProgress.value
        when {
            progress > 0.75f && !hasTriggeredConfirmHaptic -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                hasTriggeredConfirmHaptic = true
            }
            progress > 0.5f && !hasTriggeredProgressHaptic -> {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                hasTriggeredProgressHaptic = true
            }
            progress < 0.3f -> {
                hasTriggeredProgressHaptic = false
                hasTriggeredConfirmHaptic = false
            }
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp)),
        backgroundContent = {
            val direction = dismissDirection.value
            val progress = animatedProgress.value

            val color by animateColorAsState(
                targetValue = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> doneGreen
                    SwipeToDismissBoxValue.EndToStart -> pendingBlue
                    else -> Color.Transparent
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "swipe_background_color"
            )

            // Smooth eased alpha curve - icon appears earlier
            val iconAlpha = remember(progress) {
                if (progress < 0.15f) 0f
                else ((progress - 0.15f) / 0.85f).coerceIn(0f, 1f)
            }

            // Text fades in later than icon
            val textAlpha = remember(progress) {
                if (progress < 0.3f) 0f
                else ((progress - 0.3f) / 0.7f).coerceIn(0f, 1f)
            }

            // Spring-scale for satisfying pop effect
            val targetScale = if (progress > 0.2f) 1f else 0.5f
            val animatedScale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = 400f
                ),
                label = "icon_scale"
            )

            // Subtle rotation based on swipe direction
            val rotation = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> animatedScale * 5f * progress
                SwipeToDismissBoxValue.EndToStart -> -animatedScale * 5f * progress
                else -> 0f
            }

            // Background fill with smooth opacity curve
            val backgroundAlpha = remember(progress) {
                (0.08f + (progress * 0.92f)).coerceIn(0f, 1f)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color.copy(alpha = backgroundAlpha)),
                contentAlignment = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    else -> Alignment.Center
                }
            ) {
                if (direction != SwipeToDismissBoxValue.Settled) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .alpha(iconAlpha),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (direction == SwipeToDismissBoxValue.EndToStart) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Icon(
                            imageVector = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Snooze
                                else -> Icons.Default.Check
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .scale(animatedScale)
                                .graphicsLayer { rotationZ = rotation },
                            tint = color
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> "Done"
                                SwipeToDismissBoxValue.EndToStart -> "Snooze"
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            ),
                            color = color,
                            modifier = Modifier.alpha(textAlpha)
                        )

                        if (direction == SwipeToDismissBoxValue.StartToEnd) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        },
        content = {
            ReminderCard(reminder = reminder)
        }
    )
}
