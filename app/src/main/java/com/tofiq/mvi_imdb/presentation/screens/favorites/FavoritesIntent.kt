package com.tofiq.mvi_imdb.presentation.screens.favorites

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Favorites screen.
 * Represents user actions that trigger state changes.
 * 
 * Requirements: 8.1 - WHEN a user action occurs THEN the View SHALL emit an Intent to the ViewModel
 * Requirements: 4.4 - WHEN the FavoritesViewModel needs to navigate to movie details 
 *                     THEN the FavoritesViewModel SHALL emit a navigation Effect
 */
sealed interface FavoritesIntent : MviIntent {
    /** Load all favorite movies from local database */
    data object LoadFavorites : FavoritesIntent
    
    /** Remove a movie from favorites */
    data class RemoveFavorite(val movie: Movie) : FavoritesIntent
    
    /** Navigate to movie detail screen */
    data class MovieClicked(val movieId: Int) : FavoritesIntent
}
