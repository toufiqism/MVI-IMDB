package com.tofiq.mvi_imdb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        MovieEntity::class, 
        FavoriteEntity::class, 
        MovieDetailEntity::class,
        SearchEntity::class,
        CastMovieEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun movieDetailDao(): MovieDetailDao
    abstract fun searchDao(): SearchDao
    abstract fun castMovieDao(): CastMovieDao
}
