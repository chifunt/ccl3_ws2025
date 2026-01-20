package com.chifunt.chromaticharptabs.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val stored = prefs[THEME_MODE_KEY]
        if (stored != null) {
            ThemeMode.fromStorage(stored)
        } else {
            val legacyDark = prefs[DARK_THEME_KEY]
            when (legacyDark) {
                true -> ThemeMode.DARK
                false -> ThemeMode.LIGHT
                null -> ThemeMode.SYSTEM
            }
        }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.storageValue
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }
}
