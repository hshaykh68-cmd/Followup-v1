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
import com.followup.presentation.theme.doneGreen
import com.followup.presentation.theme.pendingBlue

@Composable
fun EmptyState(
    filter: ReminderFilter,
    modifier: Modifier = Modifier
) {
    val (icon, title, subtitle, iconColor) = when (filter) {
        ReminderFilter.PENDING -> Quadruple(
            Icons.Outlined.Inbox,
            "All caught up!",
            "You have no pending replies. Tap + when you need to follow up.",
            pendingBlue
        )
        ReminderFilter.TODAY -> Quadruple(
            Icons.Outlined.Schedule,
            "Nothing for today",
            "Your day is clear. Enjoy the peace of mind!",
            pendingBlue
        )
        ReminderFilter.OVERDUE -> Quadruple(
            Icons.Outlined.SentimentSatisfied,
            "No overdue replies",
            "You're staying on top of everything. Great job!",
            doneGreen
        )
        ReminderFilter.DONE -> Quadruple(
            Icons.Outlined.CheckCircle,
            "No completed yet",
            "Reminders you mark as done will appear here.",
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        ReminderFilter.ALL -> Quadruple(
            Icons.Outlined.Inbox,
            "Start your inbox",
            "Tap + below to add your first reminder to reply later.",
            pendingBlue
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon container
        val infiniteTransition = rememberInfiniteTransition(label = "float")
        val float by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float"
        )

        Box(
            modifier = Modifier
                .offset(y = float.dp)
                .size(80.dp)
                .background(
                    color = iconColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = iconColor.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = MaterialTheme.typography.titleSmall.lineHeight
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
