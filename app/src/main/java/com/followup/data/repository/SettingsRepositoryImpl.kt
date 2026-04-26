package com.followup.data.repository

import com.followup.data.local.SettingsDataStore
import com.followup.domain.repository.ReminderRepository
import com.followup.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * COMPREHENSIVE SETTINGS REPOSITORY IMPLEMENTATION
 * Bridges DataStore with domain layer, delegates data clearing to ReminderRepository
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val reminderRepository: ReminderRepository
) : SettingsRepository {

    // ==================== APPEARANCE ====================
    override fun isDarkModeEnabled(): Flow<Boolean> = settingsDataStore.darkModeEnabled

    override fun getThemeMode(): Flow<String> = settingsDataStore.themeMode

    override suspend fun setDarkMode(enabled: Boolean) {
        settingsDataStore.setDarkMode(enabled)
    }

    override suspend fun setThemeMode(mode: String) {
        settingsDataStore.setThemeMode(mode)
    }

    // ==================== NOTIFICATIONS ====================
    override fun areNotificationsEnabled(): Flow<Boolean> = settingsDataStore.notificationsEnabled

    override fun getReminderStyle(): Flow<String> = settingsDataStore.reminderStyle

    override fun isAggressiveNotificationsEnabled(): Flow<Boolean> = settingsDataStore.aggressiveNotifications

    override fun getDefaultSnoozeTime(): Flow<Long> = settingsDataStore.defaultSnoozeTime

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.setNotificationsEnabled(enabled)
    }

    override suspend fun setReminderStyle(style: String) {
        settingsDataStore.setReminderStyle(style)
    }

    override suspend fun setAggressiveNotifications(enabled: Boolean) {
        settingsDataStore.setAggressiveNotifications(enabled)
    }

    override suspend fun setDefaultSnoozeTime(durationMillis: Long) {
        settingsDataStore.setDefaultSnoozeTime(durationMillis)
    }

    // ==================== GENERAL ====================
    override fun getDefaultReminderTime(): Flow<Long> = settingsDataStore.defaultReminderTime

    override fun getDefaultTab(): Flow<String> = settingsDataStore.defaultTab

    override suspend fun setDefaultReminderTime(durationMillis: Long) {
        settingsDataStore.setDefaultReminderTime(durationMillis)
    }

    override suspend fun setDefaultTab(tab: String) {
        settingsDataStore.setDefaultTab(tab)
    }

    // ==================== REMINDERS ====================
    override fun areRepeatRemindersEnabled(): Flow<Boolean> = settingsDataStore.repeatRemindersEnabled

    override fun getAutoOverdueBehavior(): Flow<String> = settingsDataStore.autoOverdueBehavior

    override suspend fun setRepeatReminders(enabled: Boolean) {
        settingsDataStore.setRepeatReminders(enabled)
    }

    override suspend fun setAutoOverdueBehavior(behavior: String) {
        settingsDataStore.setAutoOverdueBehavior(behavior)
    }

    // ==================== HAPTICS & SOUND ====================
    override fun areHapticsEnabled(): Flow<Boolean> = settingsDataStore.hapticsEnabled

    override fun isNotificationSoundEnabled(): Flow<Boolean> = settingsDataStore.notificationSoundEnabled

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        settingsDataStore.setHapticsEnabled(enabled)
    }

    override suspend fun setNotificationSound(enabled: Boolean) {
        settingsDataStore.setNotificationSound(enabled)
    }

    // ==================== DATA & CONTROL ====================
    override suspend fun clearAllReminders() {
        // Get all reminders and delete them
        reminderRepository.getAllReminders().collect { reminders ->
            reminders.forEach { reminder ->
                reminderRepository.deleteReminder(reminder.id)
            }
        }
    }

    // ==================== ONBOARDING ====================
    override fun isOnboardingCompleted(): Flow<Boolean> = settingsDataStore.onboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settingsDataStore.setOnboardingCompleted(completed)
    }

    // ==================== NOTIFICATION LISTENER ====================
    override fun isNotificationListenerEnabled(): Flow<Boolean> = settingsDataStore.notificationListenerEnabled

    override fun isNotificationListenerPromptShown(): Flow<Boolean> = settingsDataStore.notificationListenerPromptShown

    override fun isAutoCaptureEnabled(): Flow<Boolean> = settingsDataStore.autoCaptureEnabled

    override suspend fun setNotificationListenerEnabled(enabled: Boolean) {
        settingsDataStore.setNotificationListenerEnabled(enabled)
    }

    override suspend fun setNotificationListenerPromptShown(shown: Boolean) {
        settingsDataStore.setNotificationListenerPromptShown(shown)
    }

    override suspend fun setAutoCaptureEnabled(enabled: Boolean) {
        settingsDataStore.setAutoCaptureEnabled(enabled)
    }
}
