package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving favorite movies.
 * 
 * Requirements: 5.4
 * - Retrieves all saved favorite movies from local database
 * - Returns a Flow for reactive updates when favorites change
 */
class GetFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getFavorites()
    }
}
