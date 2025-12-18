package com.tofiq.mvi_imdb.presentation.screens.castmovies

import com.tofiq.mvi_imdb.presentation.base.MviEffect

/**
 * Effects for the Cast Movies screen.
 * Represents one-time events that should be consumed exactly once by the View.
 * 
 * Requirements: 5.1, 5.3
 * - Support navigation events with required parameters (movieId)
 * - Support error notifications that should not persist in state
 */
sealed interface CastMoviesEffect : MviEffect {
    
    /**
     * Navigate to movie detail screen.
     * Requirements: 5.1 - Navigation events with required parameters
     */
    data class NavigateToMovieDetail(val movieId: Int) : CastMoviesEffect
    
    /**
     * Show error message as a transient notification.
     * Requirements: 5.3 - Error notifications that should not persist in state
     */
    data class ShowError(val message: String) : CastMoviesEffect
}
