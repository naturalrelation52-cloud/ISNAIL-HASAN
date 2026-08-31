package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "habit_tracker_prefs")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class PreferencesManager(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")
        private val USER_NAME_KEY = stringPreferencesKey("user_profile_name")
        private val USER_PHOTO_PATH_KEY = stringPreferencesKey("user_profile_photo_path")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        val name = preferences[USER_NAME_KEY] ?: "Jane Doe"
        val photoPath = preferences[USER_PHOTO_PATH_KEY]
        UserProfile(
            name = name,
            photoPath = if (photoPath.isNullOrBlank()) null else photoPath
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }

    suspend fun saveUserProfile(name: String, photoPath: String?) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            if (photoPath != null) {
                preferences[USER_PHOTO_PATH_KEY] = photoPath
            } else {
                preferences.remove(USER_PHOTO_PATH_KEY)
            }
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
