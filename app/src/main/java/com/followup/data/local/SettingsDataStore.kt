package com.followup.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    val darkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    val aggressiveNotifications: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[AGGRESSIVE_NOTIFICATIONS_KEY] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setAggressiveNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AGGRESSIVE_NOTIFICATIONS_KEY] = enabled
        }
    }

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val AGGRESSIVE_NOTIFICATIONS_KEY = booleanPreferencesKey("aggressive_notifications")
    }
}
