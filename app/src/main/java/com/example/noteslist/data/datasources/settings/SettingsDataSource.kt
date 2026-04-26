package com.example.noteslist.data.datasources.settings

import com.example.noteslist.domain.models.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {
    suspend fun getCurrentSettings(): AppSettings
    fun observeSettings(): Flow<AppSettings>
    suspend fun saveSettings(settings: AppSettings)
    suspend fun clearSettings()
}
