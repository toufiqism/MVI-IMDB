package com.tofiq.mvi_imdb.di

import com.tofiq.mvi_imdb.data.repository.MovieRepositoryImpl
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations.
 * 
 * Requirements: 8.4 - Repository is the single source of truth for movie data
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}
