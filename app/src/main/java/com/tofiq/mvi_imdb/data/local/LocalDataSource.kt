package com.tofiq.mvi_imdb.data.local

import com.tofiq.mvi_imdb.data.local.dao.CastMovieDao
import com.tofiq.mvi_imdb.data.local.dao.FavoriteDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDetailDao
import com.tofiq.mvi_imdb.data.local.dao.SearchDao
import com.tofiq.mvi_imdb.data.local.entity.CastMovieEntity
import com.tofiq.mvi_imdb.data.local.entity.FavoriteEntity
import com.tofiq.mvi_imdb.data.local.entity.MovieDetailEntity
import com.tofiq.mvi_imdb.data.local.entity.MovieEntity
import com.tofiq.mvi_imdb.data.local.entity.SearchEntity
import com.tofiq.mvi_imdb.domain.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao,
    private val movieDetailDao: MovieDetailDao,
    private val searchDao: SearchDao,
    private val castMovieDao: CastMovieDao
) {
    // Movie cache operations
    fun getMoviesByCategory(category: Category): Flow<List<MovieEntity>> =
        movieDao.getMoviesByCategory(category.name)

    suspend fun getMoviesByCategoryAndPage(category: Category, page: Int): List<MovieEntity> =
        movieDao.getMoviesByCategoryAndPage(category.name, page)

    suspend fun getMovieById(movieId: Int): MovieEntity? =
        movieDao.getMovieById(movieId)

    suspend fun cacheMovies(movies: List<MovieEntity>) =
        movieDao.insertMovies(movies)

    suspend fun clearCategoryCache(category: Category) =
        movieDao.deleteMoviesByCategory(category.name)

    suspend fun clearOldCache(maxAgeMillis: Long = 24 * 60 * 60 * 1000) {
        val cutoffTime = System.currentTimeMillis() - maxAgeMillis
        movieDao.deleteOldCache(cutoffTime)
        movieDetailDao.deleteOldCache(cutoffTime)
        searchDao.deleteOldCache(cutoffTime)
        castMovieDao.deleteOldCache(cutoffTime)
    }

    suspend fun hasCache(category: Category): Boolean =
        movieDao.getMovieCountByCategory(category.name) > 0

    // Movie detail cache operations
    suspend fun getMovieDetail(movieId: Int): MovieDetailEntity? =
        movieDetailDao.getMovieDetailById(movieId)

    suspend fun cacheMovieDetail(detail: MovieDetailEntity) {
        movieDetailDao.insertMovieDetail(detail)
    }

    // Search cache operations
    suspend fun getSearchResults(query: String, page: Int): List<MovieEntity> {
        val movieIds = searchDao.getSearchResults(query, page)
        return movieIds.mapNotNull { movieDao.getMovieById(it) }
    }

    suspend fun cacheSearchResults(query: String, page: Int, movies: List<MovieEntity>) {
        val searchEntities = movies.map { SearchEntity(query = query, page = page, movieId = it.id) }
        searchDao.insertSearchResults(searchEntities)
    }

    // Cast movie cache operations
    suspend fun getCastMovies(personId: Int): List<CastMovieEntity> =
        castMovieDao.getCastMovies(personId)

    suspend fun cacheCastMovies(personId: Int, movies: List<CastMovieEntity>) {
        castMovieDao.insertCastMovies(movies)
    }

    // Favorites operations
    fun getAllFavorites(): Flow<List<FavoriteEntity>> =
        favoriteDao.getAllFavorites()

    suspend fun getFavoriteById(movieId: Int): FavoriteEntity? =
        favoriteDao.getFavoriteById(movieId)

    suspend fun isFavorite(movieId: Int): Boolean =
        favoriteDao.isFavorite(movieId)

    fun isFavoriteFlow(movieId: Int): Flow<Boolean> =
        favoriteDao.isFavoriteFlow(movieId)

    suspend fun addFavorite(favorite: FavoriteEntity) =
        favoriteDao.insertFavorite(favorite)

    suspend fun removeFavorite(movieId: Int) =
        favoriteDao.deleteFavoriteById(movieId)

    suspend fun toggleFavorite(favorite: FavoriteEntity): Boolean {
        return if (favoriteDao.isFavorite(favorite.id)) {
            favoriteDao.deleteFavoriteById(favorite.id)
            false
        } else {
            favoriteDao.insertFavorite(favorite)
            true
        }
    }
}
