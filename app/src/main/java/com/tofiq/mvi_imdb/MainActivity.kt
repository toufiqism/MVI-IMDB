package com.tofiq.mvi_imdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tofiq.mvi_imdb.data.local.SettingsDataStore
import com.tofiq.mvi_imdb.domain.model.ThemeMode
import com.tofiq.mvi_imdb.presentation.screens.MovieApp
import com.tofiq.mvi_imdb.ui.theme.MVIIMDBTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity for the Movie App.
 *
 * Requirements: 2.1, 7.1
 * - Displays category tabs for navigation (via bottom navigation)
 * - Uses Navigation3 for screen transitions
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsDataStore.settings.collectAsState(
                initial = com.tofiq.mvi_imdb.domain.model.AppSettings()
            )
            
            val isDarkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
            MVIIMDBTheme(darkTheme = isDarkTheme) {
                MovieApp(settingsDataStore = settingsDataStore)
            }
        }
    }
}

