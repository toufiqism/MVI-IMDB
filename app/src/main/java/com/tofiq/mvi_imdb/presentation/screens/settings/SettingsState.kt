package com.tofiq.mvi_imdb.presentation.screens.settings

import androidx.compose.runtime.Immutable
import com.tofiq.mvi_imdb.domain.model.ThemeMode
import com.tofiq.mvi_imdb.domain.model.ViewMode
import com.tofiq.mvi_imdb.presentation.base.MviState

/**
 * State for the Settings screen.
 */
@Immutable
data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val cardCount: Int = 20,
    val viewMode: ViewMode = ViewMode.GRID,
    val isLoading: Boolean = true
) : MviState {
    companion object {
        val Initial = SettingsState()
    }
}
