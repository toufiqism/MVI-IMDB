package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.domain.model.CastMovie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching movies featuring a specific actor.
 * 
 * Requirements: 1.2
 * - Fetches movie credits from TMDB API using the Person_ID
 */
class GetCastMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(personId: Int): Flow<Resource<List<CastMovie>>> {
        return repository.getCastMovies(personId)
    }
}
