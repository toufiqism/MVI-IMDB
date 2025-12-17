package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.domain.model.MovieDetail
import com.tofiq.mvi_imdb.presentation.base.MviState

/**
 * State for the Detail screen.
 * Represents the immutable UI state that the View renders.
 * 
 * Requirements: 8.2 - WHEN the ViewModel receives an Intent THEN the ViewModel SHALL process it and emit a new immutable State
 */
data class DetailState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val movieId: Int? = null
) : MviState {
    
    companion object {
        val Initial = DetailState()
    }
}
