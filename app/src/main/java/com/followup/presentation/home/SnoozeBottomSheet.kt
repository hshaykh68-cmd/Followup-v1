package com.followup.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.followup.presentation.components.animations.AnimatedButton
import com.followup.presentation.theme.ComponentTokens
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnoozeBottomSheet(
    onDismiss: () -> Unit,
    onSnooze: (durationMillis: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val haptic = LocalHapticFeedback.current
    
    var selectedDuration by remember { mutableLongStateOf(30 * 60 * 1000L) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = ComponentTokens.BottomSheet.radius, topEnd = ComponentTokens.BottomSheet.radius),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        dragHandle = {
            Column(
                modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xs),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(ComponentTokens.BottomSheet.dragHandleWidth)
                        .height(ComponentTokens.BottomSheet.dragHandleHeight)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(ComponentTokens.BottomSheet.dragHandleHeight / 2)
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl)
        ) {
            // HEADER GROUP: Consistent with AddReminder sheet
            Text(
                text = "Snooze Reminder",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = Spacing.xxs)
            )
            
            Text(
                text = "Remind me again in...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // CONSISTENT: Same spacing as AddReminder subtitle
                modifier = Modifier.padding(bottom = Spacing.lg)
            )

            val options = listOf(
                15 * 60 * 1000L to Pair("15", "minutes"),
                30 * 60 * 1000L to Pair("30", "minutes"),
                60 * 60 * 1000L to Pair("1", "hour")
            )

            // OPTIONS: Grouped with tight spacing
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier
                    .fillMaxWidth()
                    // SPACE FOR CTA: Same as AddReminder before button
                    .padding(bottom = Spacing.xl)
            ) {
                options.forEach { (duration, labelPair) ->
                    val (value, unit) = labelPair
                    val isSelected = selectedDuration == duration
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedDuration = duration
                            }
                            .background(
                                color = if (isSelected) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(CornerRadius.lg)
                            )
                            .padding(vertical = Spacing.md, horizontal = Spacing.xs),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isSelected) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) 
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AnimatedButton(
                onClick = {
                    onSnooze(selectedDuration)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                hapticFeedback = HapticFeedbackType.LongPress,
                content = {
                    Text(
                        text = "Snooze",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )

            // TIGHT: Cancel close to primary action
            Spacer(modifier = Modifier.height(Spacing.xs))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
