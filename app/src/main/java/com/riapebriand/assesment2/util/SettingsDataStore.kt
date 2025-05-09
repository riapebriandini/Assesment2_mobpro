package com.riapebriand.assesment2.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.riapebriand.assesment2.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "setting_preferences"
)

class SettingsDataStore(private val context: Context) {
    companion object {
        private val IS_LIST = booleanPreferencesKey("is_list")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val layoutFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LIST] ?: true
    }

    suspend fun saveLayout(isList: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LIST] = isList
        }
    }

    val selectedThemeFlow: Flow<AppTheme> = context.dataStore.data
        .map { prefs ->
            val themeName = prefs[THEME_KEY] ?: AppTheme.PINK.name
            AppTheme.valueOf(themeName)
        }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}
