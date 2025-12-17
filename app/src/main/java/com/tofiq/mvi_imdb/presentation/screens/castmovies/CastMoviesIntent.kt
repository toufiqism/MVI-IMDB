package com.tofiq.mvi_imdb.presentation.screens.castmovies

import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Cast Movies screen.
 * Represents user actions that trigger state changes.
 * 
 * Requirements: 1.1, 1.5
 * - Navigate to Cast_Movies_Screen when tapping on a cast member
 * - Display error message with retry option on API failure
 */
sealed interface CastMoviesIntent : MviIntent {
    /** Load movies for the given person/actor */
    data class LoadCastMovies(
        val personId: Int,
        val personName: String,
        val profilePath: String?
    ) : CastMoviesIntent
    
    /** Retry loading after an error */
    data object Retry : CastMoviesIntent
}
