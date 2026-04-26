package com.followup.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * COMPREHENSIVE SETTINGS DATASTORE
 * Manages all user preferences across 7 sections:
 * General, Notifications, Reminders, Appearance, Haptics & Sound, Data & Control, About
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // ==================== APPEARANCE ====================
    val darkModeEnabled: Flow<Boolean> = dataStore.data
        .map { it[DARK_MODE_KEY] ?: false }

    val themeMode: Flow<String> = dataStore.data
        .map { it[THEME_MODE_KEY] ?: THEME_SYSTEM }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[THEME_MODE_KEY] = mode }
    }

    // ==================== NOTIFICATIONS ====================
    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .map { it[NOTIFICATIONS_ENABLED_KEY] ?: true }

    val reminderStyle: Flow<String> = dataStore.data
        .map { it[REMINDER_STYLE_KEY] ?: STYLE_NORMAL }

    val aggressiveNotifications: Flow<Boolean> = dataStore.data
        .map { it[AGGRESSIVE_NOTIFICATIONS_KEY] ?: false }

    val defaultSnoozeTime: Flow<Long> = dataStore.data
        .map { it[DEFAULT_SNOOZE_KEY] ?: (30 * 60 * 1000L) } // 30 min default

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED_KEY] = enabled }
    }

    suspend fun setReminderStyle(style: String) {
        dataStore.edit { it[REMINDER_STYLE_KEY] = style }
    }

    suspend fun setAggressiveNotifications(enabled: Boolean) {
        dataStore.edit { it[AGGRESSIVE_NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setDefaultSnoozeTime(durationMillis: Long) {
        dataStore.edit { it[DEFAULT_SNOOZE_KEY] = durationMillis }
    }

    // ==================== GENERAL ====================
    val defaultReminderTime: Flow<Long> = dataStore.data
        .map { it[DEFAULT_REMINDER_TIME_KEY] ?: (60 * 60 * 1000L) } // 1 hour default

    val defaultTab: Flow<String> = dataStore.data
        .map { it[DEFAULT_TAB_KEY] ?: TAB_PENDING }

    suspend fun setDefaultReminderTime(durationMillis: Long) {
        dataStore.edit { it[DEFAULT_REMINDER_TIME_KEY] = durationMillis }
    }

    suspend fun setDefaultTab(tab: String) {
        dataStore.edit { it[DEFAULT_TAB_KEY] = tab }
    }

    // ==================== REMINDERS ====================
    val repeatRemindersEnabled: Flow<Boolean> = dataStore.data
        .map { it[REPEAT_REMINDERS_KEY] ?: false }

    val autoOverdueBehavior: Flow<String> = dataStore.data
        .map { it[AUTO_OVERDUE_KEY] ?: OVERDUE_MARK }

    suspend fun setRepeatReminders(enabled: Boolean) {
        dataStore.edit { it[REPEAT_REMINDERS_KEY] = enabled }
    }

    suspend fun setAutoOverdueBehavior(behavior: String) {
        dataStore.edit { it[AUTO_OVERDUE_KEY] = behavior }
    }

    // ==================== HAPTICS & SOUND ====================
    val hapticsEnabled: Flow<Boolean> = dataStore.data
        .map { it[HAPTICS_ENABLED_KEY] ?: true }

    val notificationSoundEnabled: Flow<Boolean> = dataStore.data
        .map { it[NOTIFICATION_SOUND_KEY] ?: true }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[HAPTICS_ENABLED_KEY] = enabled }
    }

    suspend fun setNotificationSound(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATION_SOUND_KEY] = enabled }
    }

    // ==================== ONBOARDING ====================
    val onboardingCompleted: Flow<Boolean> = dataStore.data
        .map { it[ONBOARDING_COMPLETED_KEY] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED_KEY] = completed }
    }

    // ==================== NOTIFICATION LISTENER ====================
    val notificationListenerEnabled: Flow<Boolean> = dataStore.data
        .map { it[NOTIFICATION_LISTENER_ENABLED_KEY] ?: false }

    val notificationListenerPromptShown: Flow<Boolean> = dataStore.data
        .map { it[NOTIFICATION_LISTENER_PROMPT_SHOWN_KEY] ?: false }

    val autoCaptureEnabled: Flow<Boolean> = dataStore.data
        .map { it[AUTO_CAPTURE_ENABLED_KEY] ?: true }

    suspend fun setNotificationListenerEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATION_LISTENER_ENABLED_KEY] = enabled }
    }

    suspend fun setNotificationListenerPromptShown(shown: Boolean) {
        dataStore.edit { it[NOTIFICATION_LISTENER_PROMPT_SHOWN_KEY] = shown }
    }

    suspend fun setAutoCaptureEnabled(enabled: Boolean) {
        dataStore.edit { it[AUTO_CAPTURE_ENABLED_KEY] = enabled }
    }

    // ==================== CONSTANTS ====================
    companion object {
        // Appearance
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        // Notifications
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val REMINDER_STYLE_KEY = stringPreferencesKey("reminder_style")
        private val AGGRESSIVE_NOTIFICATIONS_KEY = booleanPreferencesKey("aggressive_notifications")
        private val DEFAULT_SNOOZE_KEY = longPreferencesKey("default_snooze_time")

        const val STYLE_GENTLE = "gentle"
        const val STYLE_NORMAL = "normal"
        const val STYLE_AGGRESSIVE = "aggressive"

        const val SNOOZE_15_MIN = 15 * 60 * 1000L
        const val SNOOZE_30_MIN = 30 * 60 * 1000L
        const val SNOOZE_1_HOUR = 60 * 60 * 1000L
        const val SNOOZE_2_HOURS = 2 * 60 * 60 * 1000L

        // General
        private val DEFAULT_REMINDER_TIME_KEY = longPreferencesKey("default_reminder_time")
        private val DEFAULT_TAB_KEY = stringPreferencesKey("default_tab")

        const val TAB_PENDING = "pending"
        const val TAB_TODAY = "today"
        const val TAB_OVERDUE = "overdue"
        const val TAB_DONE = "done"

        const val REMINDER_30_MIN = 30 * 60 * 1000L
        const val REMINDER_1_HOUR = 60 * 60 * 1000L
        const val REMINDER_2_HOURS = 2 * 60 * 60 * 1000L
        const val REMINDER_4_HOURS = 4 * 60 * 60 * 1000L
        const val REMINDER_TONIGHT = -1L // Special value
        const val REMINDER_TOMORROW = -2L // Special value

        // Reminders
        private val REPEAT_REMINDERS_KEY = booleanPreferencesKey("repeat_reminders")
        private val AUTO_OVERDUE_KEY = stringPreferencesKey("auto_overdue_behavior")

        const val OVERDUE_MARK = "mark_overdue"
        const val OVERDUE_AUTO_DONE = "auto_done_after_7_days"
        const val OVERDUE_KEEP = "keep_overdue"

        // Haptics & Sound
        private val HAPTICS_ENABLED_KEY = booleanPreferencesKey("haptics_enabled")
        private val NOTIFICATION_SOUND_KEY = booleanPreferencesKey("notification_sound")

        // Onboarding
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")

        // Notification Listener
        private val NOTIFICATION_LISTENER_ENABLED_KEY = booleanPreferencesKey("notification_listener_enabled")
        private val NOTIFICATION_LISTENER_PROMPT_SHOWN_KEY = booleanPreferencesKey("notification_listener_prompt_shown")
        private val AUTO_CAPTURE_ENABLED_KEY = booleanPreferencesKey("auto_capture_enabled")

        const val DEFAULT_REMINDER_DELAY_30_MIN = 30 * 60 * 1000L
        const val DEFAULT_REMINDER_DELAY_1_HOUR = 60 * 60 * 1000L
        const val DEFAULT_REMINDER_DELAY_2_HOURS = 2 * 60 * 60 * 1000L
        const val DEFAULT_REMINDER_DELAY_4_HOURS = 4 * 60 * 60 * 1000L
        const val DEFAULT_REMINDER_DELAY_TOMORROW = -1L // Special value
    }
}
