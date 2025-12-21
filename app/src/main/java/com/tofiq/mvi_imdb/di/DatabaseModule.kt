package com.tofiq.mvi_imdb.di

import android.content.Context
import androidx.room.Room
import com.tofiq.mvi_imdb.data.local.MovieDatabase
import com.tofiq.mvi_imdb.data.local.dao.FavoriteDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDao
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
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao = database.movieDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: MovieDatabase): FavoriteDao = database.favoriteDao()
}
