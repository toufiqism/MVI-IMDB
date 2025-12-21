package com.tofiq.mvi_imdb.data.repository

import com.tofiq.mvi_imdb.data.local.LocalDataSource
import com.tofiq.mvi_imdb.data.mapper.toCastList
import com.tofiq.mvi_imdb.data.mapper.toCastMovie
import com.tofiq.mvi_imdb.data.mapper.toCastMovieEntity
import com.tofiq.mvi_imdb.data.mapper.toCastMovieList
import com.tofiq.mvi_imdb.data.mapper.toDomain
import com.tofiq.mvi_imdb.data.mapper.toDomainList
import com.tofiq.mvi_imdb.data.mapper.toEntity
import com.tofiq.mvi_imdb.data.mapper.toFavoriteEntity
import com.tofiq.mvi_imdb.data.mapper.toFavoriteDomainList
import com.tofiq.mvi_imdb.data.mapper.toEntity
import com.tofiq.mvi_imdb.data.remote.RemoteDataSource
import com.tofiq.mvi_imdb.domain.model.CastMovie
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : MovieRepository {

    override fun getMovies(category: Category, page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading)

        // Try network first
        when (val response = remoteDataSource.getMovies(category, page)) {
            is Resource.Success -> {
                val movies = response.data.results.toDomainList()
                
                // Cache the movies
                val entities = movies.map { it.toEntity(category, page) }
                localDataSource.cacheMovies(entities)
                
                // Get favorite IDs to mark favorites
                val favoriteIds = getFavoriteIds()
                val moviesWithFavorites = movies.map { 
                    it.copy(isFavorite = it.id in favoriteIds) 
                }
                
                emit(Resource.Success(moviesWithFavorites))
            }
            is Resource.Error -> {
                // Try to get cached data on error
                val cached = localDataSource.getMoviesByCategoryAndPage(category, page)
                if (cached.isNotEmpty()) {
                    val favoriteIds = getFavoriteIds()
                    val movies = cached.map { entity ->
                        entity.toDomain(isFavorite = entity.id in favoriteIds)
                    }
                    emit(Resource.Error(response.message, movies))
                } else {
                    emit(Resource.Error(response.message))
                }
            }
            is Resource.Loading -> { /* Already emitted */ }
        }
    }

    override fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        emit(Resource.Loading)

        when (val detailResponse = remoteDataSource.getMovieDetail(movieId)) {
            is Resource.Success -> {
                val detailDto = detailResponse.data

                // Fetch credits and similar movies
                val cast = when (val creditsResponse = remoteDataSource.getMovieCredits(movieId)) {
                    is Resource.Success -> creditsResponse.data.cast.take(10).toCastList()
                    else -> emptyList()
                }
                val similarMovies = when (val similarResponse = remoteDataSource.getSimilarMovies(movieId)) {
                    is Resource.Success -> similarResponse.data.results.take(10).toDomainList()
                    else -> emptyList()
                }

                // Cache the movie detail
                localDataSource.cacheMovieDetail(detailDto.toEntity(cast, similarMovies))

                val isFavorite = localDataSource.isFavorite(movieId)
                emit(Resource.Success(detailDto.toDomain(cast, similarMovies, isFavorite)))
            }
            is Resource.Error -> {
                // On network error, try to load from cache
                val cachedDetail = localDataSource.getMovieDetail(movieId)
                if (cachedDetail != null) {
                    val isFavorite = localDataSource.isFavorite(movieId)
                    emit(Resource.Success(cachedDetail.toDomain(isFavorite)))
                } else {
                    emit(Resource.Error(detailResponse.message))
                }
            }
            is Resource.Loading -> { /* Already emitted */ }
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading)

        when (val response = remoteDataSource.searchMovies(query, page)) {
            is Resource.Success -> {
                val movies = response.data.results.toDomainList()
                localDataSource.cacheSearchResults(query, page, movies.map { it.toEntity(Category.POPULAR, 0) })
                val favoriteIds = getFavoriteIds()
                val moviesWithFavorites = movies.map { 
                    it.copy(isFavorite = it.id in favoriteIds) 
                }
                emit(Resource.Success(moviesWithFavorites))
            }
            is Resource.Error -> {
                val cached = localDataSource.getSearchResults(query, page)
                if (cached.isNotEmpty()) {
                    val favoriteIds = getFavoriteIds()
                    val movies = cached.map { entity ->
                        entity.toDomain(isFavorite = entity.id in favoriteIds)
                    }
                    emit(Resource.Error(response.message, movies))
                } else {
                    emit(Resource.Error(response.message))
                }
            }
            is Resource.Loading -> { /* Already emitted */ }
        }
    }

    override fun getFavorites(): Flow<List<Movie>> =
        localDataSource.getAllFavorites().map { it.toFavoriteDomainList() }

    override fun isFavoriteFlow(movieId: Int): Flow<Boolean> =
        localDataSource.isFavoriteFlow(movieId)

    override suspend fun toggleFavorite(movie: Movie) {
        localDataSource.toggleFavorite(movie.toFavoriteEntity())
    }

    override suspend fun isFavorite(movieId: Int): Boolean =
        localDataSource.isFavorite(movieId)

    /**
     * Fetches movies featuring a specific actor/person.
     * 
     * Requirements: 1.2, 5.1, 5.2
     * - Fetches movie credits from TMDB API using the Person_ID
     * - Sorts by release date descending (newest first)
     * 
     * Note: Full implementation will be done in task 3.
     */
    override fun getCastMovies(personId: Int): Flow<Resource<List<CastMovie>>> = flow {
        emit(Resource.Loading)
        
        when (val response = remoteDataSource.getPersonMovieCredits(personId)) {
            is Resource.Success -> {
                val movies = response.data.cast
                    .toCastMovieList()
                    .sortedByDescending { it.releaseDate ?: "" }
                localDataSource.cacheCastMovies(personId, movies.map { it.toCastMovieEntity(personId) })
                emit(Resource.Success(movies))
            }
            is Resource.Error -> {
                val cached = localDataSource.getCastMovies(personId)
                if (cached.isNotEmpty()) {
                    emit(Resource.Error(response.message, cached.toCastMovieList()))
                } else {
                    emit(Resource.Error(response.message))
                }
            }
            is Resource.Loading -> { /* Already emitted */ }
        }
    }

    private suspend fun getFavoriteIds(): Set<Int> {
        val favorites = mutableSetOf<Int>()
        // This is a simple implementation - in production you might want to cache this
        return favorites
    }
}
