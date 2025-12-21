package com.tofiq.mvi_imdb.di

import com.tofiq.mvi_imdb.data.local.LocalDataSource
import com.tofiq.mvi_imdb.data.local.dao.CastMovieDao
import com.tofiq.mvi_imdb.data.local.dao.FavoriteDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDetailDao
import com.tofiq.mvi_imdb.data.local.dao.SearchDao
import com.tofiq.mvi_imdb.data.remote.RemoteDataSource
import com.tofiq.mvi_imdb.data.remote.api.TmdbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing application-level dependencies.
 * 
 * Requirements: 8.4 - Repository is the single source of truth for movie data
 * This module provides data sources that are used by the repository.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides RemoteDataSource for API communication.
     * Requirements: 9.1 - Network client for TMDB API communication
     */
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        apiService: TmdbApiService
    ): RemoteDataSource = RemoteDataSource(apiService)

    /**
     * Provides LocalDataSource for database operations.
     * Requirements: 6.1 - Local database for offline caching and favorites storage
     */
    @Provides
    @Singleton
    fun provideLocalDataSource(
        movieDao: MovieDao,
        favoriteDao: FavoriteDao,
        movieDetailDao: MovieDetailDao,
        searchDao: SearchDao,
        castMovieDao: CastMovieDao
    ): LocalDataSource = LocalDataSource(movieDao, favoriteDao, movieDetailDao, searchDao, castMovieDao)
}