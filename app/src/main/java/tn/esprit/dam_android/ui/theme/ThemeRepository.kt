// ui/theme/ThemeRepository.kt
package tn.esprit.dam_android.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

open class ThemeRepository(context: Context) {
    private val dataStore = context.themeDataStore
    private val IS_DARK_KEY = booleanPreferencesKey("is_dark_theme")

    open val isDarkFlow: Flow<Boolean> = dataStore.data
        .map { it[IS_DARK_KEY] ?: false }

    suspend fun saveTheme(isDark: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_DARK_KEY] = isDark
        }
    }
}