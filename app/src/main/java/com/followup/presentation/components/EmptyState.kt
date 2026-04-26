package com.followup.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.followup.R
import com.followup.presentation.reminder.ReminderFilter
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Spacing

/**
 * PREMIUM EMPTY STATE - 2026 Design
 * Refined visuals, stronger text hierarchy, contextual personality
 */
@Composable
fun EmptyState(
    filter: ReminderFilter,
    modifier: Modifier = Modifier
) {
    val state = when (filter) {
        ReminderFilter.PENDING -> EmptyStateContent(
            icon = Icons.Outlined.Inbox,
            title = "All caught up!",
            subtitle = "No pending replies. Tap New below when you need to follow up.",
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            iconColor = MaterialTheme.colorScheme.primary
        )
        ReminderFilter.TODAY -> EmptyStateContent(
            icon = Icons.Outlined.Schedule,
            title = "Today is clear",
            subtitle = "No reminders scheduled. Your day is yours to enjoy!",
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            iconColor = MaterialTheme.colorScheme.secondary
        )
        ReminderFilter.OVERDUE -> EmptyStateContent(
            icon = Icons.Outlined.CheckCircle,
            title = "Excellent work!",
            subtitle = "No overdue replies. You're staying on top of everything.",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
            iconColor = MaterialTheme.colorScheme.tertiary
        )
        ReminderFilter.DONE -> EmptyStateContent(
            icon = Icons.Outlined.BeachAccess,
            title = "Fresh start",
            subtitle = "Completed reminders will appear here. Keep going!",
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ReminderFilter.ALL -> EmptyStateContent(
            icon = Icons.Outlined.AddCircle,
            title = "Welcome to FollowUp",
            subtitle = "Your personal inbox for replies. Tap New to get started.",
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            iconColor = MaterialTheme.colorScheme.primary
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // PREMIUM ICON CONTAINER: Larger, with soft shadow and animation
        val infiniteTransition = rememberInfiniteTransition(label = "float")
        val float by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = androidx.compose.animation.core.EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float"
        )

        Box(
            modifier = Modifier
                .offset(y = float.dp)
                .size(96.dp)
                .background(
                    color = state.containerColor,
                    shape = RoundedCornerShape(CornerRadius.xxl)
                )
                .padding(Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = state.icon,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = state.iconColor
            )
        }

        // STRONG HIERARCHY: Prominent title, clear subtitle
        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = state.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
    }
}

private data class EmptyStateContent(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val containerColor: androidx.compose.ui.graphics.Color,
    val iconColor: androidx.compose.ui.graphics.Color
)

