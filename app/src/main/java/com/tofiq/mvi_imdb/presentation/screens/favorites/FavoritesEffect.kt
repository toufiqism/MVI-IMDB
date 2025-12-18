package com.tofiq.mvi_imdb.presentation.screens.favorites

import com.tofiq.mvi_imdb.presentation.base.MviEffect

/**
 * Effects for the Favorites screen.
 * 
 * These represent one-time events that should be consumed exactly once by the View,
 * such as navigation events and transient messages.
 * 
 * Requirements: 5.1, 5.2
 */
sealed interface FavoritesEffect : MviEffect {
    
    /**
     * Navigate to a movie's detail screen.
     * Requirements: 5.1 - Navigation events with required parameters
     */
    data class NavigateToMovieDetail(val movieId: Int) : FavoritesEffect
    
    /**
     * Show a message when a favorite is removed.
     * Requirements: 5.2 - Support showing transient messages
     */
    data class ShowFavoriteRemoved(val movieTitle: String) : FavoritesEffect
}
