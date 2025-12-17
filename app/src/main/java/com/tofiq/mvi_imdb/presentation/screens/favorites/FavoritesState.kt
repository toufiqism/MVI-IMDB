package com.tofiq.mvi_imdb.presentation.screens.favorites

import androidx.compose.runtime.Immutable
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.presentation.base.MviState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * State for the Favorites screen.
 * Represents the immutable UI state that the View renders.
 * Marked as @Immutable with ImmutableList to optimize recompositions.
 * 
 * Requirements: 8.2 - WHEN the ViewModel receives an Intent THEN the ViewModel SHALL process it and emit a new immutable State
 */
@Immutable
data class FavoritesState(
    val favorites: ImmutableList<Movie> = persistentListOf(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
) : MviState {
    
    companion object {
        val Initial = FavoritesState()
    }
}
