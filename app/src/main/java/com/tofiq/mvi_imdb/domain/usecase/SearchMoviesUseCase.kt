package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case for searching movies by title.
 * 
 * Requirements: 3.2, 3.3
 * - Searches movies from TMDB API by query string
 * - Requires minimum 2 characters to trigger search
 * - Supports pagination for search results
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(query: String, page: Int = 1): Flow<Resource<List<Movie>>> {
        // Requirement 3.2: Search query must be at least 2 characters
        if (query.length < 2) {
            return flowOf(Resource.Success(emptyList()))
        }
        return repository.searchMovies(query, page)
    }
}
