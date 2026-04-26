package com.example.noteslist.domain.usecases.settings

import com.example.noteslist.domain.models.AppSettings
import com.example.noteslist.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAppSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.observeSettings()
}
