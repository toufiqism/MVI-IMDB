package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Detail screen.
 * Represents user actions that trigger state changes.
 * 
 * Requirements: 8.1 - WHEN a user action occurs THEN the View SHALL emit an Intent to the ViewModel
 */
sealed interface DetailIntent : MviIntent {
    /** Load movie details for the given movie ID */
    data class LoadDetail(val movieId: Int) : DetailIntent
    
    /** Toggle the favorite status of the current movie */
    data object ToggleFavorite : DetailIntent
    
    /** Retry loading after an error */
    data object Retry : DetailIntent
}
