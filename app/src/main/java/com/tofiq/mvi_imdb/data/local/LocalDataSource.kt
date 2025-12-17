package com.tofiq.mvi_imdb.data.local

import com.tofiq.mvi_imdb.data.local.dao.FavoriteDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDao
import com.tofiq.mvi_imdb.data.local.entity.FavoriteEntity
import com.tofiq.mvi_imdb.data.local.entity.MovieEntity
import com.tofiq.mvi_imdb.domain.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao
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
    }

    suspend fun hasCache(category: Category): Boolean =
        movieDao.getMovieCountByCategory(category.name) > 0

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
