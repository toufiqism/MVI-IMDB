package com.tofiq.mvi_imdb.presentation.screens.home

import com.tofiq.mvi_imdb.presentation.base.MviEffect

/**
 * Effects for the Home screen.
 * 
 * These represent one-time events that should be consumed exactly once by the View,
 * such as navigation events and error notifications.
 * 
 * Requirements: 5.1, 5.3
 */
sealed interface HomeEffect : MviEffect {
    
    /**
     * Navigate to a movie's detail screen.
     * Requirements: 5.1 - Navigation events with required parameters
     */
    data class NavigateToMovieDetail(val movieId: Int) : HomeEffect
    
    /**
     * Show an error message that should not persist in state.
     * Requirements: 5.3 - Support error notifications that should not persist in state
     */
    data class ShowError(val message: String) : HomeEffect
}
