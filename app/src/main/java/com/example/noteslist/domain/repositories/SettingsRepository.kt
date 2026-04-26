package com.example.noteslist.domain.repositories

import com.example.noteslist.domain.models.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    suspend fun resetSettings()
}
