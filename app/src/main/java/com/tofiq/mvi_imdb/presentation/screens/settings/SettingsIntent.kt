package com.tofiq.mvi_imdb.presentation.screens.settings

import com.tofiq.mvi_imdb.domain.model.ThemeMode
import com.tofiq.mvi_imdb.domain.model.ViewMode
import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Settings screen.
 */
sealed interface SettingsIntent : MviIntent {
    data object LoadSettings : SettingsIntent
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsIntent
    data class UpdateCardCount(val count: Int) : SettingsIntent
    data class UpdateViewMode(val viewMode: ViewMode) : SettingsIntent
}
