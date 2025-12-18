package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Detail screen.
 * Represents user actions that trigger state changes.
 * 
 * Requirements: 8.1 - WHEN a user action occurs THEN the View SHALL emit an Intent to the ViewModel
 * Requirements: 4.1 - Navigation to cast movies via Effects
 */
sealed interface DetailIntent : MviIntent {
    /** Load movie details for the given movie ID */
    data class LoadDetail(val movieId: Int) : DetailIntent
    
    /** Toggle the favorite status of the current movie */
    data object ToggleFavorite : DetailIntent
    
    /** Retry loading after an error */
    data object Retry : DetailIntent
    
    /** Navigate to cast movies screen for a specific actor */
    data class CastClicked(
        val personId: Int,
        val personName: String,
        val profilePath: String?
    ) : DetailIntent
    
    /** Navigate to another movie's detail screen */
    data class SimilarMovieClicked(val movieId: Int) : DetailIntent
}
