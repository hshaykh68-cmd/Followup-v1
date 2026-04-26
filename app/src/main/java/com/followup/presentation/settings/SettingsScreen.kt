package com.followup.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AppSettingsAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.followup.R
import com.followup.data.local.SettingsDataStore
import com.followup.presentation.theme.Screen
import com.followup.presentation.theme.Spacing

/**
 * COMPLETE SETTINGS SCREEN
 * 7 organized sections with premium UI components
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // Collect all state flows
    val themeMode by viewModel.themeMode.collectAsState()
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val reminderStyle by viewModel.reminderStyle.collectAsState()
    val aggressiveNotifications by viewModel.aggressiveNotifications.collectAsState()
    val defaultSnoozeTime by viewModel.defaultSnoozeTime.collectAsState()
    val defaultReminderTime by viewModel.defaultReminderTime.collectAsState()
    val defaultTab by viewModel.defaultTab.collectAsState()
    val repeatRemindersEnabled by viewModel.repeatRemindersEnabled.collectAsState()
    val autoOverdueBehavior by viewModel.autoOverdueBehavior.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val notificationSoundEnabled by viewModel.notificationSoundEnabled.collectAsState()
    val autoCaptureEnabled by viewModel.autoCaptureEnabled.collectAsState()

    // Confirmation dialog state
    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Screen.paddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Spacer(modifier = Modifier.height(Spacing.sm))

        // ==================== GENERAL ====================
        SettingsSection(title = stringResource(R.string.settings_general)) {
            ValueSettingItem(
                icon = Icons.Default.Schedule,
                title = "Default Reminder Time",
                subtitle = "Time preset for new reminders",
                value = formatDuration(defaultReminderTime),
                onClick = { /* Navigate to time selector */ }
            )
            SettingDivider()
            ValueSettingItem(
                icon = Icons.Default.Tab,
                title = "Default Tab",
                subtitle = "Tab shown when opening app",
                value = formatTabName(defaultTab),
                onClick = { /* Navigate to tab selector */ }
            )
        }

        // ==================== NOTIFICATIONS ====================
        SettingsSection(title = stringResource(R.string.settings_notifications)) {
            SwitchSettingItem(
                icon = if (notificationsEnabled) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                title = "Enable Reminders",
                subtitle = "Receive follow-up notifications",
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
            )
            SettingDivider()
            SwitchSettingItem(
                icon = if (autoCaptureEnabled) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                title = "Auto-Capture from Messages",
                subtitle = "One-tap reminders from WhatsApp, SMS, etc.",
                checked = autoCaptureEnabled,
                onCheckedChange = { viewModel.setAutoCaptureEnabled(it) }
            )
            SettingDivider()
            ValueSettingItem(
                icon = Icons.Default.AppSettingsAlt,
                title = "Reminder Style",
                subtitle = "Notification intensity",
                value = formatStyleName(reminderStyle),
                onClick = { /* Navigate to style selector */ }
            )
            SettingDivider()
            ValueSettingItem(
                icon = Icons.Default.AccessTime,
                title = "Default Snooze",
                subtitle = "Time added when snoozing",
                value = formatDuration(defaultSnoozeTime),
                onClick = { /* Navigate to snooze selector */ }
            )
        }

        // ==================== REMINDERS ====================
        SettingsSection(title = stringResource(R.string.settings_reminders)) {
            SwitchSettingItem(
                icon = Icons.Default.Repeat,
                title = "Repeat Reminders",
                subtitle = "Remind again if not marked done",
                checked = repeatRemindersEnabled,
                onCheckedChange = { viewModel.setRepeatReminders(it) }
            )
            SettingDivider()
            ValueSettingItem(
                icon = Icons.Default.EditNote,
                title = "Overdue Behavior",
                subtitle = "How to handle overdue reminders",
                value = formatOverdueBehavior(autoOverdueBehavior),
                onClick = { /* Navigate to behavior selector */ }
            )
        }

        // ==================== APPEARANCE ====================
        SettingsSection(title = stringResource(R.string.settings_appearance)) {
            ValueSettingItem(
                icon = Icons.Default.Palette,
                title = "Theme",
                subtitle = "App color scheme",
                value = formatThemeName(themeMode),
                onClick = { /* Navigate to theme selector */ }
            )
            SettingDivider()
            SwitchSettingItem(
                icon = if (darkModeEnabled) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
                title = "Dark Mode",
                subtitle = "Override system theme",
                checked = darkModeEnabled,
                onCheckedChange = { viewModel.setDarkMode(it) }
            )
        }

        // ==================== HAPTICS & SOUND ====================
        SettingsSection(title = "Haptics & Sound") {
            SwitchSettingItem(
                icon = Icons.Default.Vibration,
                title = "Vibration Feedback",
                subtitle = "Haptic response on interactions",
                checked = hapticsEnabled,
                onCheckedChange = { viewModel.setHapticsEnabled(it) }
            )
            SettingDivider()
            SwitchSettingItem(
                icon = Icons.Default.VolumeUp,
                title = "Notification Sound",
                subtitle = "Play sound with reminders",
                checked = notificationSoundEnabled,
                onCheckedChange = { viewModel.setNotificationSound(it) }
            )
        }

        // ==================== DATA & CONTROL ====================
        SettingsSection(title = "Data & Control") {
            DangerSettingItem(
                icon = Icons.Default.DeleteForever,
                title = "Clear All Reminders",
                subtitle = "Permanently delete all reminders",
                onClick = { showClearDataDialog = true }
            )
        }

        // ==================== ABOUT & LEGAL ====================
        SettingsSection(title = stringResource(R.string.settings_about)) {
            AboutCard(
                appName = stringResource(R.string.app_name),
                description = "Your personal follow-up assistant. Never forget to reply again.",
                version = "1.0.0"
            )
        }

        SettingsSection(title = "Legal") {
            NavigationSettingItem(
                icon = Icons.Default.Policy,
                title = "Privacy Policy",
                subtitle = null,
                onClick = { /* Open privacy policy */ }
            )
            SettingDivider()
            NavigationSettingItem(
                icon = Icons.AutoMirrored.Filled.Help,
                title = "Terms & Conditions",
                subtitle = null,
                onClick = { /* Open terms */ }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = { androidx.compose.material3.Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Clear All Reminders?") },
            text = { Text("This will permanently delete all your reminders. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllReminders { showClearDataDialog = false }
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==================== FORMATTING HELPERS ====================

private fun formatDuration(millis: Long): String = when (millis) {
    SettingsDataStore.REMINDER_30_MIN, SettingsDataStore.SNOOZE_15_MIN -> "15 min"
    SettingsDataStore.REMINDER_1_HOUR, SettingsDataStore.SNOOZE_30_MIN -> "30 min"
    SettingsDataStore.REMINDER_2_HOURS, SettingsDataStore.SNOOZE_1_HOUR -> "1 hour"
    SettingsDataStore.REMINDER_4_HOURS, SettingsDataStore.SNOOZE_2_HOURS -> "2 hours"
    SettingsDataStore.REMINDER_TONIGHT -> "Tonight"
    SettingsDataStore.REMINDER_TOMORROW -> "Tomorrow"
    else -> "Custom"
}

private fun formatTabName(tab: String): String = when (tab) {
    SettingsDataStore.TAB_PENDING -> "Pending"
    SettingsDataStore.TAB_TODAY -> "Today"
    SettingsDataStore.TAB_OVERDUE -> "Overdue"
    SettingsDataStore.TAB_DONE -> "Done"
    else -> "Pending"
}

private fun formatStyleName(style: String): String = when (style) {
    SettingsDataStore.STYLE_GENTLE -> "Gentle"
    SettingsDataStore.STYLE_NORMAL -> "Normal"
    SettingsDataStore.STYLE_AGGRESSIVE -> "Aggressive"
    else -> "Normal"
}

private fun formatOverdueBehavior(behavior: String): String = when (behavior) {
    SettingsDataStore.OVERDUE_MARK -> "Mark Overdue"
    SettingsDataStore.OVERDUE_AUTO_DONE -> "Auto-complete after 7 days"
    SettingsDataStore.OVERDUE_KEEP -> "Keep as Pending"
    else -> "Mark Overdue"
}

private fun formatThemeName(theme: String): String = when (theme) {
    SettingsDataStore.THEME_LIGHT -> "Light"
    SettingsDataStore.THEME_DARK -> "Dark"
    SettingsDataStore.THEME_SYSTEM -> "System Default"
    else -> "System Default"
}
