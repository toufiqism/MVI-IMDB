package com.tofiq.mvi_imdb.presentation.screens.favorites

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.presentation.base.MviState

/**
 * State for the Favorites screen.
 * Represents the immutable UI state that the View renders.
 * 
 * Requirements: 8.2 - WHEN the ViewModel receives an Intent THEN the ViewModel SHALL process it and emit a new immutable State
 */
data class FavoritesState(
    val favorites: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
) : MviState {
    
    companion object {
        val Initial = FavoritesState()
    }
}
