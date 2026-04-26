package com.followup.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.followup.data.local.SettingsDataStore
import com.followup.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * COMPREHENSIVE SETTINGS VIEWMODEL
 * Exposes reactive state for all 7 settings sections
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // ==================== APPEARANCE ====================
    val darkModeEnabled: StateFlow<Boolean> = settingsRepository.isDarkModeEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeMode: StateFlow<String> = settingsRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.THEME_SYSTEM)

    // ==================== NOTIFICATIONS ====================
    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.areNotificationsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val reminderStyle: StateFlow<String> = settingsRepository.getReminderStyle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.STYLE_NORMAL)

    val aggressiveNotifications: StateFlow<Boolean> = settingsRepository.isAggressiveNotificationsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val defaultSnoozeTime: StateFlow<Long> = settingsRepository.getDefaultSnoozeTime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.SNOOZE_30_MIN)

    // ==================== GENERAL ====================
    val defaultReminderTime: StateFlow<Long> = settingsRepository.getDefaultReminderTime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.REMINDER_1_HOUR)

    val defaultTab: StateFlow<String> = settingsRepository.getDefaultTab()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.TAB_PENDING)

    // ==================== REMINDERS ====================
    val repeatRemindersEnabled: StateFlow<Boolean> = settingsRepository.areRepeatRemindersEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val autoOverdueBehavior: StateFlow<String> = settingsRepository.getAutoOverdueBehavior()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.OVERDUE_MARK)

    // ==================== HAPTICS & SOUND ====================
    val hapticsEnabled: StateFlow<Boolean> = settingsRepository.areHapticsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notificationSoundEnabled: StateFlow<Boolean> = settingsRepository.isNotificationSoundEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // ==================== AUTO-CAPTURE ====================
    val autoCaptureEnabled: StateFlow<Boolean> = settingsRepository.isAutoCaptureEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // ==================== SETTERS ====================

    // Appearance
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    // Notifications
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }

    fun setReminderStyle(style: String) {
        viewModelScope.launch { settingsRepository.setReminderStyle(style) }
    }

    fun setAggressiveNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAggressiveNotifications(enabled) }
    }

    fun setDefaultSnoozeTime(durationMillis: Long) {
        viewModelScope.launch { settingsRepository.setDefaultSnoozeTime(durationMillis) }
    }

    // General
    fun setDefaultReminderTime(durationMillis: Long) {
        viewModelScope.launch { settingsRepository.setDefaultReminderTime(durationMillis) }
    }

    fun setDefaultTab(tab: String) {
        viewModelScope.launch { settingsRepository.setDefaultTab(tab) }
    }

    // Reminders
    fun setRepeatReminders(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setRepeatReminders(enabled) }
    }

    fun setAutoOverdueBehavior(behavior: String) {
        viewModelScope.launch { settingsRepository.setAutoOverdueBehavior(behavior) }
    }

    // Haptics & Sound
    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHapticsEnabled(enabled) }
    }

    fun setNotificationSound(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationSound(enabled) }
    }

    // Auto-Capture
    fun setAutoCaptureEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoCaptureEnabled(enabled) }
    }

    // Data & Control
    fun clearAllReminders(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            settingsRepository.clearAllReminders()
            onComplete()
        }
    }
}
