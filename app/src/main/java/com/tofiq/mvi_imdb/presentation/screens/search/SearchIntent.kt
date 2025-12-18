package com.tofiq.mvi_imdb.presentation.screens.search

import com.tofiq.mvi_imdb.presentation.base.MviIntent

/**
 * Intents for the Search screen.
 * Represents user actions that trigger state changes.
 * 
 * Requirements: 8.1 - WHEN a user action occurs THEN the View SHALL emit an Intent to the ViewModel
 */
sealed interface SearchIntent : MviIntent {
    /** Update the search query text */
    data class UpdateQuery(val query: String) : SearchIntent
    
    /** Clear the search input and results */
    data object ClearSearch : SearchIntent
    
    /** Load the next page of search results (pagination) */
    data object LoadNextPage : SearchIntent
    
    /** Navigate to movie detail screen */
    data class MovieClicked(val movieId: Int) : SearchIntent
}
