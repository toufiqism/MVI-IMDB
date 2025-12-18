package com.tofiq.mvi_imdb.presentation.screens.home

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Home screen.
 * Represents user actions that trigger state changes.
 * 
 * Requirements: 8.1 - WHEN a user action occurs THEN the View SHALL emit an Intent to the ViewModel
 */
sealed interface HomeIntent : MviIntent {
    /** Load initial movies for the current category */
    data object LoadMovies : HomeIntent
    
    /** Select a different category tab */
    data class SelectCategory(val category: Category) : HomeIntent
    
    /** Load the next page of movies (pagination) */
    data object LoadNextPage : HomeIntent
    
    /** Retry loading after an error */
    data object Retry : HomeIntent
    
    /** User clicked on a movie to view details */
    data class MovieClicked(val movieId: Int) : HomeIntent
}
