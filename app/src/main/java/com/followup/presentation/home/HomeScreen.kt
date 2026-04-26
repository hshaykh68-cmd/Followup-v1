package com.followup.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
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
import com.followup.presentation.theme.AnimationDuration
import com.followup.presentation.theme.ComponentTokens
import com.followup.presentation.theme.CornerRadius
import com.followup.presentation.theme.Elevation
import com.followup.presentation.theme.ScaleValues
import com.followup.presentation.theme.Screen
import com.followup.presentation.theme.Spacing
import com.followup.presentation.theme.Stagger
import kotlinx.coroutines.delay
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
            PremiumAnimatedFAB(
                onClick = { 
                    showAddBottomSheet = true 
                },
                pendingCount = pendingCount
            )
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

            // MODERN PILL-STYLE TABS: Floating tab bar with animated selection pill
            ModernTabRow(
                tabs = tabs,
                selectedFilter = uiState.selectedFilter,
                onTabSelected = { filter ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setFilter(filter)
                }
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // PREMIUM TAB CONTENT: Slide + fade with direction based on tab order
                AnimatedContent(
                    targetState = uiState.selectedFilter,
                    transitionSpec = {
                        val targetIndex = targetState.ordinal
                        val initialIndex = initialState.ordinal
                        val slideDirection = if (targetIndex > initialIndex) 1 else -1
                        
                        (fadeIn(
                            animationSpec = tween(250, easing = FastOutSlowInEasing)
                        ) + slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetX = { fullWidth -> fullWidth / 5 * slideDirection }
                        )) togetherWith (
                            fadeOut(
                                animationSpec = tween(200, easing = FastOutSlowInEasing)
                            ) + slideOutHorizontally(
                                animationSpec = tween(200, easing = FastOutSlowInEasing),
                                targetOffsetX = { fullWidth -> -fullWidth / 5 * slideDirection }
                            )
                        )
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
                    // TOP PADDING: Small offset from tabs (8dp)
                    // BOTTOM PADDING: Space for FAB clearance (80dp)
                    contentPadding = PaddingValues(top = Spacing.xs, bottom = 80.dp)
                ) {
                    itemsIndexed(
                        items = reminders,
                        key = { index, reminder -> "${filter.name}_${reminder.id}" }
                    ) { index, reminder ->
                        PremiumAnimatedListItem(
                            reminder = reminder,
                            index = index,
                            totalItems = reminders.size,
                            onMarkDone = onMarkDone,
                            onSnooze = onSnooze
                        )
                    }
                }
            }
        }
    }
}

/**
 * PREMIUM ANIMATED LIST ITEM
 * Staggered entrance with fade + slide + scale
 */
@Composable
private fun PremiumAnimatedListItem(
    reminder: Reminder,
    index: Int,
    totalItems: Int,
    onMarkDone: (Reminder) -> Unit,
    onSnooze: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val slideDistancePx = remember { with(density) { 40.dp.toPx() } }
    
    // Calculate stagger delay
    val staggerDelay = remember(index, totalItems) {
        val rawDelay = index * Stagger.LIST_ITEM_DELAY
        if (totalItems > 6) {
            (rawDelay.coerceAtMost(Stagger.MAX_STAGGER_TIME) * (6f / totalItems)).toLong()
        } else {
            rawDelay.coerceAtMost(Stagger.MAX_STAGGER_TIME)
        }
    }
    
    // Animation states
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.92f) }
    val offsetY = remember { Animatable(slideDistancePx) }
    var hasAnimatedIn by remember { mutableStateOf(false) }

    // Staggered entrance animation
    LaunchedEffect(reminder.id) {
        // Reset for new items
        alpha.snapTo(0f)
        scale.snapTo(0.92f)
        offsetY.snapTo(slideDistancePx)
        hasAnimatedIn = false
        
        delay(staggerDelay)
        
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = AnimationDuration.EMPHASIZED,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        hasAnimatedIn = true
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                translationY = offsetY.value
            }
    ) {
        SwipeableReminderItem(
            reminder = reminder,
            onDone = { onMarkDone(reminder) },
            onSnooze = { onSnooze(reminder) }
        )
    }
}

/**
 * PREMIUM ANIMATED TAB ROW
 * Sliding indicator pill with press feedback
 */
@Composable
private fun ModernTabRow(
    tabs: List<Pair<ReminderFilter, String>>,
    selectedFilter: ReminderFilter,
    onTabSelected: (ReminderFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val selectedIndex = tabs.indexOfFirst { it.first == selectedFilter }
    
    // Track tab positions for sliding indicator
    val tabPositions = remember { mutableStateListOf<Float>() }
    val tabWidths = remember { mutableStateListOf<Float>() }
    
    // Indicator animation
    val indicatorOffset = remember { Animatable(0f) }
    val indicatorWidth = remember { Animatable(0f) }

    // Animate indicator to selected tab
    LaunchedEffect(selectedIndex, tabPositions, tabWidths) {
        if (selectedIndex >= 0 && selectedIndex < tabPositions.size) {
            val targetOffset = tabPositions[selectedIndex]
            val targetWidth = tabWidths[selectedIndex]
            
            launch {
                indicatorOffset.animateTo(
                    targetValue = targetOffset,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                indicatorWidth.animateTo(
                    targetValue = targetWidth,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        // Floating container with subtle background
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(CornerRadius.xl)
                )
                .padding(Spacing.xs),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Sliding indicator pill (positioned absolutely)
            Box(
                modifier = Modifier
                    .offset(x = with(density) { indicatorOffset.value.toDp() })
                    .width(with(density) { indicatorWidth.value.toDp() })
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(CornerRadius.lg)
                    )
            )

            // Tab buttons overlay
            tabs.forEachIndexed { index, (filter, label) ->
                val isSelected = selectedIndex == index
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale = remember { Animatable(1f) }

                // Press feedback
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        scale.animateTo(
                            targetValue = ScaleValues.PRESSED,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessHigh
                            )
                        )
                    } else {
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale.value)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (filter != selectedFilter) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(filter)
                            }
                        }
                        .onGloballyPositioned { layoutCoordinates ->
                            val position = layoutCoordinates.positionInParent().x
                            val width = layoutCoordinates.size.width.toFloat()
                            if (tabPositions.size > index) {
                                tabPositions[index] = position
                                tabWidths[index] = width
                            } else {
                                tabPositions.add(position)
                                tabWidths.add(width)
                            }
                        }
                        .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated text weight
                    val animatedWeight by animateFloatAsState(
                        targetValue = if (isSelected) 600f else 500f,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessMedium,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        ),
                        label = "tab_weight"
                    )

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight(animatedWeight.toInt()),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

/**
 * PREMIUM ANIMATED FAB
 * Entrance animation + press feedback + optional pulse when items pending
 */
@Composable
private fun PremiumAnimatedFAB(
    onClick: () -> Unit,
    pendingCount: Int,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Entrance animation
    val entranceScale = remember { Animatable(0f) }
    val entranceAlpha = remember { Animatable(0f) }
    
    // Press animation
    val pressScale = remember { Animatable(1f) }
    val elevation = remember { Animatable(Elevation.high.value) }
    
    // Pulse animation for attention when there are pending items
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (pendingCount > 0) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Entrance animation - pop in with bounce
    LaunchedEffect(Unit) {
        entranceScale.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 500
                0f at 0
                1.1f at 300
                1f at 500
            }
        )
        entranceAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
    }

    // Press feedback
    LaunchedEffect(isPressed) {
        if (isPressed) {
            launch {
                pressScale.animateTo(
                    targetValue = ScaleValues.EMPHASIZED_PRESSED,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
            launch {
                elevation.animateTo(
                    targetValue = Elevation.raised.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }
        } else {
            launch {
                pressScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                elevation.animateTo(
                    targetValue = Elevation.high.value,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }

    val finalScale = if (!isPressed && pendingCount > 0) pulseScale else 1f
    val combinedScale = entranceScale.value * pressScale.value * finalScale

    Box(
        modifier = modifier
            .height(56.dp)
            .padding(end = Spacing.xs, bottom = Spacing.md)
            .graphicsLayer {
                scaleX = combinedScale
                scaleY = combinedScale
                alpha = entranceAlpha.value
                shadowElevation = elevation.value
            }
            .shadow(
                elevation = elevation.value.dp,
                shape = RoundedCornerShape(CornerRadius.xl),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(CornerRadius.xl)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "New",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
