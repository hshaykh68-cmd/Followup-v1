package com.followup.presentation.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Confirm
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.SegmentFrequentTick
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.followup.domain.model.Reminder
import com.followup.presentation.theme.doneGreen
import com.followup.presentation.theme.pendingBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableReminderItem(
    reminder: Reminder,
    onDone: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentOnDone by rememberUpdatedState(onDone)
    val currentOnSnooze by rememberUpdatedState(onSnooze)
    var hasTriggeredHaptic by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    haptic.performHapticFeedback(Confirm)
                    currentOnSnooze()
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptic.performHapticFeedback(Confirm)
                    currentOnDone()
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.4f }
    )

    // Progress-based haptic (subtle feedback at 50% threshold)
    LaunchedEffect(dismissState.progress) {
        if (dismissState.progress > 0.5f && !hasTriggeredHaptic) {
            haptic.performHapticFeedback(SegmentFrequentTick)
            hasTriggeredHaptic = true
        } else if (dismissState.progress < 0.3f) {
            hasTriggeredHaptic = false
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp)),
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val progress = dismissState.progress.coerceIn(0f, 1f)

            val color by animateColorAsState(
                targetValue = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> doneGreen
                    SwipeToDismissBoxValue.EndToStart -> pendingBlue
                    else -> Color.Transparent
                },
                animationSpec = tween(200),
                label = "swipe_background_color"
            )

            val contentAlpha by animateFloatAsState(
                targetValue = if (direction == SwipeToDismissBoxValue.Settled) 0f else progress,
                animationSpec = spring(),
                label = "content_alpha"
            )

            val scale by animateFloatAsState(
                targetValue = if (progress > 0.2f) 1f else 0.6f,
                animationSpec = spring(stiffness = 300f),
                label = "icon_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color.copy(alpha = 0.15f + (0.85f * progress))),
                contentAlignment = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    else -> Alignment.Center
                }
            ) {
                if (direction != SwipeToDismissBoxValue.Settled) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 28.dp)
                            .alpha(contentAlpha),
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
                            modifier = Modifier.scale(scale),
                            tint = color
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> "Done"
                                SwipeToDismissBoxValue.EndToStart -> "Snooze"
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = color,
                            modifier = Modifier.alpha(progress)
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
