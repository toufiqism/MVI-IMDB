package com.tofiq.mvi_imdb.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import com.tofiq.mvi_imdb.domain.model.ThemeMode
import com.tofiq.mvi_imdb.domain.model.ViewMode
import com.tofiq.mvi_imdb.domain.repository.SettingsRepository
import com.tofiq.mvi_imdb.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen following MVI architecture.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : MviViewModel<SettingsIntent, SettingsState, SettingsEffect>() {

    private val _state = MutableStateFlow(SettingsState.Initial)
    override val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        processIntent(SettingsIntent.LoadSettings)
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadSettings -> loadSettings()
            is SettingsIntent.UpdateThemeMode -> updateThemeMode(intent.themeMode)
            is SettingsIntent.UpdateCardCount -> updateCardCount(intent.count)
            is SettingsIntent.UpdateViewMode -> updateViewMode(intent.viewMode)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        themeMode = settings.themeMode,
                        cardCount = settings.cardCount,
                        viewMode = settings.viewMode,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(themeMode)
        }
    }

    private fun updateCardCount(count: Int) {
        viewModelScope.launch {
            settingsRepository.updateCardCount(count)
        }
    }

    private fun updateViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            settingsRepository.updateViewMode(viewMode)
        }
    }
}
