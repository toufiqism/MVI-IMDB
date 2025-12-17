package com.tofiq.mvi_imdb.presentation.screens.search

import androidx.compose.runtime.Immutable
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.presentation.base.MviState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * State for the Search screen.
 * Represents the immutable UI state that the View renders.
 * Marked as @Immutable with ImmutableList to optimize recompositions.
 * 
 * Requirements: 8.2 - WHEN the ViewModel receives an Intent THEN the ViewModel SHALL process it and emit a new immutable State
 */
@Immutable
data class SearchState(
    val query: String = "",
    val movies: ImmutableList<Movie> = persistentListOf(),
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
