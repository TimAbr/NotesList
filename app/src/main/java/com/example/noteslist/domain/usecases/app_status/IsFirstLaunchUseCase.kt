package com.example.noteslist.domain.usecases.app_status

import com.example.noteslist.domain.repositories.AppStatusRepository
import javax.inject.Inject

class IsFirstLaunchUseCase @Inject constructor(
    private val repository: AppStatusRepository
) {
    operator fun invoke(): Boolean = repository.isFirstLaunch()
}
