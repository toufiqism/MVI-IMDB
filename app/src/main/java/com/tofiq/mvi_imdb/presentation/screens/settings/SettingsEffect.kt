package com.tofiq.mvi_imdb.presentation.screens.settings

import com.tofiq.mvi_imdb.presentation.base.MviEffect

/**
 * Effects for the Settings screen.
 */
sealed interface SettingsEffect : MviEffect {
    data class ShowMessage(val message: String) : SettingsEffect
}
