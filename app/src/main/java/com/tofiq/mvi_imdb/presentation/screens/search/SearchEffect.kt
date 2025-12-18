package com.tofiq.mvi_imdb.presentation.screens.search

import com.tofiq.mvi_imdb.presentation.base.MviEffect

/**
 * Effects for the Search screen.
 * 
 * These represent one-time events that should be consumed exactly once by the View,
 * such as navigation events.
 * 
 * Requirements: 5.1
 */
sealed interface SearchEffect : MviEffect {
    
    /**
     * Navigate to a movie's detail screen.
     * Requirements: 5.1 - Navigation events with required parameters
     */
    data class NavigateToMovieDetail(val movieId: Int) : SearchEffect
}
