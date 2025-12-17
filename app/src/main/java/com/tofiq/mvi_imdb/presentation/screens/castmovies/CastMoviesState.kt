package com.tofiq.mvi_imdb.presentation.screens.castmovies

import androidx.compose.runtime.Immutable
import com.tofiq.mvi_imdb.domain.model.CastMovie
import com.tofiq.mvi_imdb.presentation.base.MviState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * State for the Cast Movies screen.
 * Represents the immutable UI state that the View renders.
 * Marked as @Immutable to optimize recompositions.
 * 
 * Requirements: 1.3, 1.4, 1.5, 2.1, 2.2
 * - Display movies in a grid format
 * - Display loading indicator during API request
 * - Display error message with retry option on failure
 * - Show actor's name in header
 * - Show actor's profile photo if available
 */
@Immutable
data class CastMoviesState(
    val movies: ImmutableList<CastMovie> = persistentListOf(),
    val personName: String = "",
    val profilePath: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val personId: Int? = null
) : MviState {
    
    companion object {
        val Initial = CastMoviesState()
    }
}
