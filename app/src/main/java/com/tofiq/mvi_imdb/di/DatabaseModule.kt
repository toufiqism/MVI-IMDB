package com.tofiq.mvi_imdb.di

import android.content.Context
import androidx.room.Room
import com.tofiq.mvi_imdb.data.local.MovieDatabase
import com.tofiq.mvi_imdb.data.local.dao.CastMovieDao
import com.tofiq.mvi_imdb.data.local.dao.FavoriteDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDetailDao
import com.tofiq.mvi_imdb.data.local.dao.SearchDao
import com.tofiq.mvi_imdb.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        Constants.DATABASE_NAME
    ).fallbackToDestructiveMigration(true).build()

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao = database.movieDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: MovieDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    @Singleton
    fun provideMovieDetailDao(database: MovieDatabase): MovieDetailDao = database.movieDetailDao()

    @Provides
    @Singleton
    fun provideSearchDao(database: MovieDatabase): SearchDao = database.searchDao()

    @Provides
    @Singleton
    fun provideCastMovieDao(database: MovieDatabase): CastMovieDao = database.castMovieDao()
}
