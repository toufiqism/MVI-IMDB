package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.domain.model.MovieDetail
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching movie details with cast information.
 * 
 * Requirements: 4.1, 4.2, 4.3, 4.4
 * - Fetches detailed movie information including backdrop, poster, title, release date, runtime, genres, rating, overview
 * - Includes cast list with actor photos and character names
 * - Includes similar movie recommendations
 */
class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetail>> {
        return repository.getMovieDetail(movieId)
    }
}
