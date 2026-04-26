package com.followup.presentation.stats

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.followup.R
import com.followup.presentation.reminder.ReminderViewModel
import com.followup.presentation.theme.ComponentTokens
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Screen
import com.followup.presentation.theme.Spacing

@Composable
fun StatsScreen(
    viewModel: ReminderViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()

    val completedCount = remember(uiState.reminders) {
        uiState.reminders.count { it.isDone }
    }
    val totalCount = uiState.reminders.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Screen.paddingHorizontal),
        // REDUCED: Tighter spacing between cards for cohesive feel
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // HERO STATS: Big numbers, clean layout, minimal colors
        ModernStreakCard(streakDays = 0)
        
        ModernStatsOverview(
            pending = pendingCount,
            completed = completedCount,
            total = totalCount
        )

        // COMPACT SECTION HEADER: Minimal space, tight to content
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            // TIGHT: Small left indent, minimal bottom spacing
            modifier = Modifier.padding(start = Spacing.xs, bottom = Spacing.xxs, top = Spacing.xs)
        )

        StatDetailCard(
            icon = Icons.Default.PendingActions,
            title = "Pending",
            value = pendingCount,
            color = MaterialTheme.colorScheme.primary,
            description = "Reminders waiting for you"
        )

        StatDetailCard(
            icon = Icons.Default.CheckCircle,
            title = "Completed",
            value = completedCount,
            color = MaterialTheme.colorScheme.tertiary,
            description = "Reminders marked as done"
        )

        StatDetailCard(
            icon = Icons.Default.Schedule,
            title = "Total",
            value = totalCount,
            color = MaterialTheme.colorScheme.secondary,
            description = "All reminders created"
        )
    }
}

/**
 * MODERN STREAK CARD
 * Clean hero card with strong number emphasis and subtle accent
 */
@Composable
private fun ModernStreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    var animatedStreak by remember { mutableIntStateOf(0) }
    val animatedValue by animateIntAsState(
        targetValue = streakDays,
        animationSpec = tween(durationMillis = 1000),
        label = "streak_animation"
    )

    LaunchedEffect(animatedValue) {
        animatedStreak = animatedValue
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(CornerRadius.xxl),
                ambientColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(CornerRadius.xxl),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.ml),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // PREMIUM ICON CONTAINER: Large, tonal background
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(CornerRadius.xl))
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.width(Spacing.lg))

            Column(modifier = Modifier.weight(1f)) {
                // HERO NUMBER: Big, bold streak count
                Text(
                    text = animatedStreak.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "day streak",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // MODERN PROGRESS RING
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(Spacing.xs),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { (animatedStreak % 7) / 7f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.tertiary,
                    strokeWidth = 5.dp,
                    trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                )
                Text(
                    text = "${(animatedStreak % 7)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

/**
 * MODERN STATS OVERVIEW
 * Clean horizontal layout with consistent neutral styling
 */
@Composable
private fun ModernStatsOverview(
    pending: Int,
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(CornerRadius.xxl)
            ),
        shape = RoundedCornerShape(CornerRadius.xxl),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.lg, horizontal = Spacing.md),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModernStatItem(
                value = pending,
                label = "Pending",
                accentColor = MaterialTheme.colorScheme.primary
            )
            ModernStatItem(
                value = completed,
                label = "Done",
                accentColor = MaterialTheme.colorScheme.tertiary
            )
            ModernStatItem(
                value = total,
                label = "Total",
                accentColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * MODERN STAT ITEM
 * Clean vertical stat with accent dot and strong number
 */
@Composable
private fun ModernStatItem(
    value: Int,
    label: String,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ACCENT DOT: Small, subtle color indicator
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        // BIG NUMBER: Featured value with bold weight
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xxs))
        // LABEL: Clean secondary text
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatDetailCard(
    icon: ImageVector,
    title: String,
    value: Int,
    color: Color,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.xl),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        // TIGHT PADDING: 16dp all around for stat detail cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(ComponentTokens.Icon.sizeLg),
                    tint = color
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                // CARD TITLE: Item name
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                // DESCRIPTION: Secondary info
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // VALUE: Right-aligned number with emphasis
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            )
        }
    }
}
