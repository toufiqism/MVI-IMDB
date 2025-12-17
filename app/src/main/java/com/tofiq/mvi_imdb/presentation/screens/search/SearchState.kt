package com.tofiq.mvi_imdb.presentation.screens.search

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.presentation.base.MviState

/**
 * State for the Search screen.
 * Represents the immutable UI state that the View renders.
 * 
 * Requirements: 8.2 - WHEN the ViewModel receives an Intent THEN the ViewModel SHALL process it and emit a new immutable State
 */
data class SearchState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false
) : MviState {
    
    companion object {
        val Initial = SearchState()
    }
}
