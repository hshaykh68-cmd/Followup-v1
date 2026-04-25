package com.followup.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.followup.R
import com.followup.domain.model.Reminder
import com.followup.presentation.components.EmptyState
import com.followup.presentation.components.SwipeableReminderItem
import com.followup.presentation.reminder.ReminderFilter
import com.followup.presentation.reminder.ReminderViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ReminderViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var showSnoozeBottomSheet by remember { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf<Reminder?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = tween(300)),
                exit = scaleOut(animationSpec = tween(300))
            ) {
                FloatingActionButton(
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showAddBottomSheet = true 
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_reminder_title)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val tabs = listOf(
                ReminderFilter.PENDING to stringResource(R.string.tab_pending),
                ReminderFilter.TODAY to stringResource(R.string.tab_today),
                ReminderFilter.OVERDUE to stringResource(R.string.tab_overdue),
                ReminderFilter.DONE to stringResource(R.string.tab_done)
            )

            PrimaryTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == uiState.selectedFilter },
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabs.forEach { (filter, label) ->
                    Tab(
                        selected = uiState.selectedFilter == filter,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setFilter(filter)
                        },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (uiState.selectedFilter == filter) 
                                        FontWeight.SemiBold else FontWeight.Medium
                                )
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = uiState.selectedFilter,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith 
                        fadeOut(animationSpec = tween(300))
                    },
                    label = "tab_content"
                ) { targetFilter ->
                    ReminderList(
                        reminders = uiState.filteredReminders,
                        filter = targetFilter,
                        isLoading = uiState.isLoading,
                        onMarkDone = { reminder ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.markAsDone(reminder.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Marked as done")
                            }
                        },
                        onSnooze = { reminder ->
                            selectedReminder = reminder
                            showSnoozeBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    if (showAddBottomSheet) {
        AddReminderBottomSheet(
            onDismiss = { showAddBottomSheet = false },
            onSave = { name, message, time ->
                viewModel.addReminder(
                    name = name,
                    message = message,
                    reminderTime = time,
                    onSuccess = {
                        showAddBottomSheet = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Reminder saved")
                        }
                    },
                    onError = { error ->
                        scope.launch {
                            snackbarHostState.showSnackbar(error)
                        }
                    }
                )
            }
        )
    }

    if (showSnoozeBottomSheet && selectedReminder != null) {
        SnoozeBottomSheet(
            onDismiss = { 
                showSnoozeBottomSheet = false
                selectedReminder = null
            },
            onSnooze = { durationMillis ->
                selectedReminder?.let { reminder ->
                    viewModel.snoozeReminder(
                        reminderId = reminder.id,
                        durationMillis = durationMillis,
                        onSuccess = {
                            showSnoozeBottomSheet = false
                            selectedReminder = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Snoozed")
                            }
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun ReminderList(
    reminders: List<Reminder>,
    filter: ReminderFilter,
    isLoading: Boolean,
    onMarkDone: (Reminder) -> Unit,
    onSnooze: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
            reminders.isEmpty() -> {
                EmptyState(filter = filter)
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(
                        items = reminders,
                        key = { "${filter.name}_${it.id}" }
                    ) { reminder ->
                        AnimatedListItem(
                            reminder = reminder,
                            onMarkDone = onMarkDone,
                            onSnooze = onSnooze
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedListItem(
    reminder: Reminder,
    onMarkDone: (Reminder) -> Unit,
    onSnooze: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val transitionState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    val transition = updateTransition(transitionState, label = "item_transition")

    val offset by transition.animateFloat(
        label = "offset",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { visible ->
        if (visible) 0f else 100f
    }

    val alpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = { tween(300) }
    ) { visible ->
        if (visible) 1f else 0f
    }

    val scale by transition.animateFloat(
        label = "scale",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { visible ->
        if (visible) 1f else 0.9f
    }

    Box(
        modifier = modifier
            .offset(y = offset.dp)
            .alpha(alpha)
            .then(Modifier.graphicsLayer { scaleX = scale; scaleY = scale })
    ) {
        SwipeableReminderItem(
            reminder = reminder,
            onDone = { onMarkDone(reminder) },
            onSnooze = { onSnooze(reminder) }
        )
    }
}
