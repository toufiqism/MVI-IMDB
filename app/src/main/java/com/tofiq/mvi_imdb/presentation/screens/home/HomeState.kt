package com.tofiq.mvi_imdb.presentation.screens.home

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.presentation.base.MviState

/**
 * State for the Home screen.
 * Represents the immutable UI state that the View renders.
 * 
 * Requirements: 8.2 - WHEN the ViewModel receives an Intent THEN the ViewModel SHALL process it and emit a new immutable State
 */
data class HomeState(
    val movies: List<Movie> = emptyList(),
    val selectedCategory: Category = Category.POPULAR,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) : MviState {
    
    companion object {
        val Initial = HomeState()
    }
}
