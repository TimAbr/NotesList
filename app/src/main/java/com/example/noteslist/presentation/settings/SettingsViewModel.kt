package com.example.noteslist.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.models.AppSettings
import com.example.noteslist.domain.usecases.settings.ObserveAppSettingsUseCase
import com.example.noteslist.domain.usecases.settings.ResetAppSettingsUseCase
import com.example.noteslist.domain.usecases.settings.UpdateAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeAppSettingsUseCase: ObserveAppSettingsUseCase,
    private val updateAppSettingsUseCase: UpdateAppSettingsUseCase,
    private val resetAppSettingsUseCase: ResetAppSettingsUseCase
) : ViewModel() {

    val settings = observeAppSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, AppSettings.default())

    fun saveSettings(spacing: Int, maxVisible: Int) {
        viewModelScope.launch {
            updateAppSettingsUseCase(AppSettings(spacing, maxVisible))
        }
    }

    fun reset() {
        viewModelScope.launch {
            resetAppSettingsUseCase()
        }
    }
}
