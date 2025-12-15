package com.strikes.busgapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val CURRENCY = stringPreferencesKey("currency")
        val START_OF_WEEK = intPreferencesKey("start_of_week") // 0 = Sunday, 1 = Monday
        val THEME = stringPreferencesKey("theme")
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    val currency: Flow<String> = dataStore.data.map { preferences ->
        preferences[CURRENCY] ?: "$"
    }

    val startOfWeek: Flow<Int> = dataStore.data.map { preferences ->
        preferences[START_OF_WEEK] ?: 1 // Default to Monday
    }

    val theme: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME] ?: "Blue"
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY] = currency
        }
    }

    suspend fun setStartOfWeek(dayOfWeek: Int) {
        dataStore.edit { preferences ->
            preferences[START_OF_WEEK] = dayOfWeek
        }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun resetAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

