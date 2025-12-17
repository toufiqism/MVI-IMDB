package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching movies by category.
 * 
 * Requirements: 1.1, 2.2
 * - Fetches movies from TMDB API by category (Popular, Top Rated, Upcoming, Now Playing)
 * - Supports pagination for loading more movies
 */
class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(category: Category, page: Int = 1): Flow<Resource<List<Movie>>> {
        return repository.getMovies(category, page)
    }
}
