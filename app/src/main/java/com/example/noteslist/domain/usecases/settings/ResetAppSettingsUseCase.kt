package com.example.noteslist.domain.usecases.settings

import com.example.noteslist.domain.repositories.SettingsRepository
import javax.inject.Inject

class ResetAppSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        repository.resetSettings()
    }
}
