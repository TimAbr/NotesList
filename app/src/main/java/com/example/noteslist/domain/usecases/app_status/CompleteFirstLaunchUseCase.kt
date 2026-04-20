package com.example.noteslist.domain.usecases.app_status

import com.example.noteslist.domain.repositories.AppStatusRepository
import javax.inject.Inject

class CompleteFirstLaunchUseCase @Inject constructor(
    private val repository: AppStatusRepository
) {
    operator fun invoke() {
        repository.markFirstLaunchComplete()
    }
}
