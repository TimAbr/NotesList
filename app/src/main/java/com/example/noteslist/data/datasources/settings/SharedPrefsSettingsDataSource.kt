package com.example.noteslist.data.datasources.settings

import android.content.Context
import android.content.SharedPreferences
import com.example.noteslist.domain.models.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
@BoundTo(SettingsDataSource::class)
class SharedPrefsSettingsDataSource @Inject constructor(
    @ApplicationContext context: Context
) : SettingsDataSource {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override suspend fun getCurrentSettings(): AppSettings {
        return readSettings(prefs)
    }

    private val listeners = mutableSetOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun observeSettings(): Flow<AppSettings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
            if (key == null || key == KEY_STACK_SPACING || key == KEY_STACK_MAX_VISIBLE) {
                trySend(readSettings(p))
            }
        }
        listeners.add(listener)
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(readSettings(prefs))
        
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
            listeners.remove(listener)
        }
    }

    override suspend fun saveSettings(settings: AppSettings) {
        prefs.edit {
            putInt(KEY_STACK_SPACING, settings.stackSpacing)
            putInt(KEY_STACK_MAX_VISIBLE, settings.stackMaxVisible)
        }
    }

    override suspend fun clearSettings() {
        prefs.edit {
            clear()
        }
    }

    private fun readSettings(p: SharedPreferences): AppSettings {
        return AppSettings(
            stackSpacing = p.getInt(
                KEY_STACK_SPACING,
                AppSettings.DEFAULT_STACK_SPACING
            ),
            stackMaxVisible = p.getInt(
                KEY_STACK_MAX_VISIBLE,
                AppSettings.DEFAULT_STACK_MAX_VISIBLE
            )
        )
    }

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_STACK_SPACING = "stack_spacing"
        private const val KEY_STACK_MAX_VISIBLE = "stack_max_visible"
    }
}
