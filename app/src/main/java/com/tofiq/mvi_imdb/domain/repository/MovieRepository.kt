package com.tofiq.mvi_imdb.domain.repository

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(category: Category, page: Int): Flow<Resource<List<Movie>>>
    fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>>
    fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>>
    fun getFavorites(): Flow<List<Movie>>
    fun isFavoriteFlow(movieId: Int): Flow<Boolean>
    suspend fun toggleFavorite(movie: Movie)
    suspend fun isFavorite(movieId: Int): Boolean
}
