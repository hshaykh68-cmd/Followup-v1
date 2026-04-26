package com.followup.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.followup.R
import com.followup.presentation.components.TimePreset
import com.followup.presentation.components.animations.AnimatedButton
import com.followup.presentation.theme.ComponentTokens
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddReminderBottomSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, message: String?, time: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val haptic = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    
    // Use rememberSaveable to survive configuration changes
    var name by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var selectedPreset by rememberSaveable { mutableStateOf(TimePreset.MINUTES_30) }
    var customTime by rememberSaveable { mutableStateOf<Long?>(null) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    // Auto-focus name field when sheet opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Computed properties for UI state
    val canSave = name.isNotBlank() && (customTime != null || selectedPreset != null)
    val effectiveTime = when {
        customTime != null -> customTime!!
        selectedPreset == TimePreset.TONIGHT -> TimePreset.getTonightTime()
        selectedPreset != null -> System.currentTimeMillis() + selectedPreset.durationMillis
        else -> System.currentTimeMillis()
    }

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
                .heightIn(max = 580.dp)
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    keyboardController?.hide()
                }
        ) {
            // HEADER GROUP: Title + subtitle compact
            Text(
                text = stringResource(R.string.add_reminder_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = Spacing.xxs)
            )
            
            Text(
                text = "Quickly capture who you need to reply to",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // REDUCED: Tighter grouping of header elements
                modifier = Modifier.padding(bottom = Spacing.lg)
            )

            // Name field - Auto-focused for <3 second entry
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    if (it.length == 1) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                },
                label = { Text("Who?") },
                placeholder = { Text("Name or contact") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { keyboardController?.hide() }
                )
            )

            // COMPACT: Tight spacing between input fields (related elements)
            Spacer(modifier = Modifier.height(Spacing.xs))

            // Message field - Optional, collapsed by default hint
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("What about? (optional)") },
                placeholder = { Text("Brief note to remember") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                )
            )

            // SECTION BREAK: Space before time selection (different grouping)
            Spacer(modifier = Modifier.height(Spacing.lg))

            // SECTION HEADER: Clear separation for time selection
            Text(
                text = "Remind me",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )

            // PREMIUM CHIPS: Rounded, clear selected state, responsive
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePreset.entries.forEach { preset ->
                    val isSelected = selectedPreset == preset && customTime == null
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedPreset = preset
                            customTime = null
                            keyboardController?.hide()
                        },
                        label = { 
                            Text(
                                text = when (preset) {
                                    TimePreset.MINUTES_30 -> "30 min"
                                    TimePreset.HOUR_1 -> "1 hour"
                                    TimePreset.TONIGHT -> "Tonight"
                                    TimePreset.TOMORROW -> "Tomorrow"
                                },
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            },
                            borderWidth = if (isSelected) 0.dp else 1.dp
                        ),
                        shape = RoundedCornerShape(CornerRadius.xl)
                    )
                }

                // Custom time chip
                val isCustomSelected = customTime != null
                FilterChip(
                    selected = isCustomSelected,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showTimePicker = true
                        keyboardController?.hide()
                    },
                    label = { 
                        Text(
                            text = customTime?.let { 
                                SimpleDateFormat("EEE, h:mm a", Locale.getDefault()).format(Date(it))
                            } ?: "Custom",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isCustomSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        )
                    },
                    leadingIcon = if (isCustomSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isCustomSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        borderWidth = if (isCustomSelected) 0.dp else 1.dp
                    ),
                    shape = RoundedCornerShape(CornerRadius.xl)
                )
            }

            // ACTION GROUP: Space before CTA
            Spacer(modifier = Modifier.height(Spacing.xl))

            // PREMIUM PRIMARY CTA: Animated button with press feedback
            AnimatedButton(
                onClick = {
                    keyboardController?.hide()
                    onSave(name.trim(), message.trim().takeIf { it.isNotBlank() }, effectiveTime)
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                hapticFeedback = HapticFeedbackType.LongPress,
                content = {
                    Text(
                        text = "Create Reminder",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            )

            // Cancel button - minimal, centered
            Spacer(modifier = Modifier.height(Spacing.xs))

            TextButton(
                onClick = {
                    keyboardController?.hide()
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }

    if (showTimePicker) {
        DateTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { timestamp ->
                customTime = timestamp
                selectedPreset = TimePreset.MINUTES_30
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(true) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        if (datePickerState.selectedDateMillis != null) {
                            showDatePicker = false
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = selectedDate
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            onConfirm(calendar.timeInMillis)
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Back")
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TimePicker(state = timePickerState)
            }
        }
    }
}

