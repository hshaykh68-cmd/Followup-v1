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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.followup.R
import com.followup.domain.model.Reminder
import com.followup.presentation.theme.doneGreen
import com.followup.presentation.theme.overdueRed
import com.followup.presentation.theme.pendingBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ReminderCard(
    reminder: Reminder,
    modifier: Modifier = Modifier
) {
    val (containerColor, statusColor, icon) = when {
        reminder.isDone -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            doneGreen,
            Icons.Default.CheckCircle
        )
        reminder.isOverdue -> Triple(
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            overdueRed,
            Icons.Default.Warning
        )
        else -> Triple(
            MaterialTheme.colorScheme.surface,
            pendingBlue,
            Icons.Default.AccessTime
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (reminder.isDone) 0.dp else 1.dp,
            pressedElevation = if (reminder.isDone) 0.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                StatusIndicator(
                    isDone = reminder.isDone,
                    isOverdue = reminder.isOverdue,
                    color = statusColor
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = reminder.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (reminder.isDone) FontWeight.Medium else FontWeight.SemiBold
                        ),
                        color = if (reminder.isDone) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!reminder.message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = reminder.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (reminder.isDone) 0.45f else 0.75f
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TimeChip(
                        timestamp = reminder.reminderTime,
                        isDone = reminder.isDone,
                        isOverdue = reminder.isOverdue
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(
    isDone: Boolean,
    isOverdue: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(
                when {
                    isDone -> color.copy(alpha = 0.3f)
                    isOverdue -> color.copy(alpha = 0.25f)
                    else -> color.copy(alpha = 0.2f)
                }
            )
            .padding(2.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun TimeChip(
    timestamp: Long,
    isDone: Boolean,
    isOverdue: Boolean,
    modifier: Modifier = Modifier
) {
    val (text, color, containerAlpha) = when {
        isDone -> Triple(
            formatTimeCompleted(timestamp),
            MaterialTheme.colorScheme.onSurfaceVariant,
            0.12f
        )
        isOverdue -> Triple(
            stringResource(R.string.time_overdue),
            overdueRed,
            0.15f
        )
        else -> Triple(
            formatTimeRemainingSmart(timestamp),
            pendingBlue,
            0.15f
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = containerAlpha))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = when {
                isDone -> Icons.Default.CheckCircle
                isOverdue -> Icons.Default.Warning
                else -> Icons.Default.AccessTime
            },
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = color
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
