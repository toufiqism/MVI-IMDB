package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.presentation.base.MviEffect

/**
 * Effects for the Detail screen.
 * 
 * These represent one-time events that should be consumed exactly once by the View,
 * such as navigation events and transient messages.
 * 
 * Requirements: 5.1, 5.2, 5.3
 */
sealed interface DetailEffect : MviEffect {
    
    /**
     * Navigate to another movie's detail screen.
     * Requirements: 5.1 - Navigation events with required parameters
     */
    data class NavigateToMovie(val movieId: Int) : DetailEffect
    
    /**
     * Navigate to the cast movies screen showing all movies for a specific actor.
     * Requirements: 5.1 - Navigation events with required parameters
     */
    data class NavigateToCastMovies(
        val personId: Int,
        val personName: String,
        val profilePath: String?
    ) : DetailEffect
    
    /**
     * Show a transient message (e.g., snackbar, toast).
     * Requirements: 5.2 - Support showing transient messages
     */
    data class ShowMessage(val message: String) : DetailEffect
    
    /**
     * Navigate back to the previous screen.
     * Requirements: 5.3 - Support error notifications
     */
    data object NavigateBack : DetailEffect
}
