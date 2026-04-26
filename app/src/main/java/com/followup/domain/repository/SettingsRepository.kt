package com.followup.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * COMPREHENSIVE SETTINGS REPOSITORY INTERFACE
 * Exposes all user preferences across 7 sections
 */
interface SettingsRepository {

    // ==================== APPEARANCE ====================
    fun isDarkModeEnabled(): Flow<Boolean>
    fun getThemeMode(): Flow<String>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setThemeMode(mode: String)

    // ==================== NOTIFICATIONS ====================
    fun areNotificationsEnabled(): Flow<Boolean>
    fun getReminderStyle(): Flow<String>
    fun isAggressiveNotificationsEnabled(): Flow<Boolean>
    fun getDefaultSnoozeTime(): Flow<Long>
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setReminderStyle(style: String)
    suspend fun setAggressiveNotifications(enabled: Boolean)
    suspend fun setDefaultSnoozeTime(durationMillis: Long)

    // ==================== GENERAL ====================
    fun getDefaultReminderTime(): Flow<Long>
    fun getDefaultTab(): Flow<String>
    suspend fun setDefaultReminderTime(durationMillis: Long)
    suspend fun setDefaultTab(tab: String)

    // ==================== REMINDERS ====================
    fun areRepeatRemindersEnabled(): Flow<Boolean>
    fun getAutoOverdueBehavior(): Flow<String>
    suspend fun setRepeatReminders(enabled: Boolean)
    suspend fun setAutoOverdueBehavior(behavior: String)

    // ==================== HAPTICS & SOUND ====================
    fun areHapticsEnabled(): Flow<Boolean>
    fun isNotificationSoundEnabled(): Flow<Boolean>
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setNotificationSound(enabled: Boolean)

    // ==================== DATA & CONTROL ====================
    suspend fun clearAllReminders()

    // ==================== ONBOARDING ====================
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    // ==================== NOTIFICATION LISTENER ====================
    fun isNotificationListenerEnabled(): Flow<Boolean>
    fun isNotificationListenerPromptShown(): Flow<Boolean>
    fun isAutoCaptureEnabled(): Flow<Boolean>
    suspend fun setNotificationListenerEnabled(enabled: Boolean)
    suspend fun setNotificationListenerPromptShown(shown: Boolean)
    suspend fun setAutoCaptureEnabled(enabled: Boolean)
}
