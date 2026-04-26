package com.example.noteslist.data.repositories

import com.example.noteslist.data.datasources.settings.SettingsDataSource
import com.example.noteslist.domain.models.AppSettings
import com.example.noteslist.domain.repositories.SettingsRepository
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(SettingsRepository::class)
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override suspend fun getSettings(): AppSettings = dataSource.getCurrentSettings()

    override fun observeSettings(): Flow<AppSettings> = dataSource.observeSettings()

    override suspend fun updateSettings(settings: AppSettings) {
        dataSource.saveSettings(settings)
    }

    override suspend fun resetSettings() {
        dataSource.clearSettings()
    }
}
