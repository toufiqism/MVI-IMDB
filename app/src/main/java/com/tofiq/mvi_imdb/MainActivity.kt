package com.tofiq.mvi_imdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tofiq.mvi_imdb.presentation.screens.MovieApp
import com.tofiq.mvi_imdb.ui.theme.MVIIMDBTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Movie App.
 *
 * Requirements: 2.1, 7.1
 * - Displays category tabs for navigation (via bottom navigation)
 * - Uses Navigation3 for screen transitions
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVIIMDBTheme {
                MovieApp()
            }
        }
    }
}

