package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Use case for toggling movie favorite status.
 * 
 * Requirements: 5.2, 5.3
 * - Adds movie to favorites if not already favorited
 * - Removes movie from favorites if already favorited
 * - Persists changes to local database immediately
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        repository.toggleFavorite(movie)
    }
}
