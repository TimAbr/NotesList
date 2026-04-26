package com.example.noteslist.domain.usecases.settings

import com.example.noteslist.domain.models.AppSettings
import com.example.noteslist.domain.repositories.SettingsRepository
import javax.inject.Inject

class UpdateAppSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(settings: AppSettings) {
        repository.updateSettings(settings)
    }
}
