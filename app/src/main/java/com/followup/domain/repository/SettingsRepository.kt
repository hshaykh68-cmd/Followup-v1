package com.followup.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkModeEnabled(): Flow<Boolean>
    fun isAggressiveNotificationsEnabled(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setAggressiveNotifications(enabled: Boolean)
}
