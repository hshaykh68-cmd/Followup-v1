package com.followup.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.followup.R
import com.followup.domain.model.Reminder
import com.followup.presentation.theme.ComponentTokens
import com.followup.presentation.theme.Content
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Elevation
import com.followup.presentation.theme.Screen
import com.followup.presentation.theme.Spacing
import com.followup.presentation.theme.statusDone
import com.followup.presentation.theme.statusOverdue
import com.followup.presentation.theme.statusPending
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * PREMIUM REMINDER CARD - 2026 Design
 * Enhanced visual depth, refined states, modern hierarchy
 */
@Composable
fun ReminderCard(
    reminder: Reminder,
    modifier: Modifier = Modifier
) {
    val (containerColor, statusColor, tonalElevation) = when {
        reminder.isDone -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            statusDone,
            Elevation.none
        )
        reminder.isOverdue -> Triple(
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
            statusOverdue,
            Elevation.low
        )
        else -> Triple(
            MaterialTheme.colorScheme.surface,
            statusPending,
            Elevation.subtle
        )
    }

    // PREMIUM CARD: Subtle shadow + tonal surface for depth
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Screen.paddingHorizontal, vertical = Spacing.xxs)
            .shadow(
                elevation = if (reminder.isDone) 0.dp else 2.dp,
                shape = RoundedCornerShape(CornerRadius.xxl),
                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(CornerRadius.xxl),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = tonalElevation,
            pressedElevation = if (reminder.isDone) Elevation.none else Elevation.low
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // BALANCED PADDING: 20dp horizontal, 16dp vertical
                .padding(horizontal = Spacing.ml, vertical = Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // MODERN STATUS: Larger, more prominent indicator
                ModernStatusIndicator(
                    isDone = reminder.isDone,
                    isOverdue = reminder.isOverdue,
                    color = statusColor
                )

                // TIGHT: Small gap between indicator and content
                Spacer(modifier = Modifier.width(Spacing.sm))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // PRIMARY: Name with strong visual weight
                    Text(
                        text = reminder.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (reminder.isDone) FontWeight.Medium else FontWeight.SemiBold,
                        color = if (reminder.isDone) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = Content.alphaDisabled)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = Content.titleMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )

                    // SECONDARY: Message - clean subordinate styling
                    if (!reminder.message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        Text(
                            text = reminder.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (reminder.isDone) {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = Content.alphaDisabled)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = Content.bodyMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // TERTIARY: Modern time chip
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    ModernTimeChip(
                        timestamp = reminder.reminderTime,
                        isDone = reminder.isDone,
                        isOverdue = reminder.isOverdue
                    )
                }
            }
        }
    }
}

/**
 * MODERN STATUS INDICATOR
 * Larger, cleaner visual state representation
 */
@Composable
private fun ModernStatusIndicator(
    isDone: Boolean,
    isOverdue: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isDone -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        isOverdue -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(3.dp)
            .clip(CircleShape)
            .background(
                when {
                    isDone -> MaterialTheme.colorScheme.outline
                    isOverdue -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }
            )
    )
}

/**
 * MODERN TIME CHIP
 * Refined pill design with subtle background and clear iconography
 */
@Composable
private fun ModernTimeChip(
    timestamp: Long,
    isDone: Boolean,
    isOverdue: Boolean,
    modifier: Modifier = Modifier
) {
    val (text, icon, backgroundColor, contentColor) = when {
        isDone -> Quadruple(
            formatTimeCompleted(timestamp),
            Icons.Default.CheckCircle,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.outline
        )
        isOverdue -> Quadruple(
            stringResource(R.string.time_overdue),
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.error
        )
        else -> Quadruple(
            formatTimeRemainingSmart(timestamp),
            Icons.Default.AccessTime,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.primary
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.xl))
            .background(backgroundColor)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = contentColor
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatTimeRemainingSmart(timestamp: Long): String {
    val diff = timestamp - System.currentTimeMillis()
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Now"
        minutes == 1L -> "1 min"
        minutes < 60 -> "$minutes mins"
        hours == 1L -> "1 hour"
        hours < 24 -> "$hours hours"
        days == 1L -> "Tomorrow"
        days < 7 -> "$days days"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatTimeCompleted(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault())
    return "Completed ${sdf.format(Date(timestamp))}"
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
