package com.followup.data.repository

import com.followup.data.local.SettingsDataStore
import com.followup.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun isDarkModeEnabled(): Flow<Boolean> {
        return settingsDataStore.darkModeEnabled
    }

    override fun isAggressiveNotificationsEnabled(): Flow<Boolean> {
        return settingsDataStore.aggressiveNotifications
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        settingsDataStore.setDarkMode(enabled)
    }

    override suspend fun setAggressiveNotifications(enabled: Boolean) {
        settingsDataStore.setAggressiveNotifications(enabled)
    }
}
