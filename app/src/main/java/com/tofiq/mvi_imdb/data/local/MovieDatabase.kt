package com.tofiq.mvi_imdb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tofiq.mvi_imdb.data.local.dao.FavoriteDao
import com.tofiq.mvi_imdb.data.local.dao.MovieDao
import com.tofiq.mvi_imdb.data.local.entity.FavoriteEntity
import com.tofiq.mvi_imdb.data.local.entity.MovieEntity

@Database(
    entities = [MovieEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favoriteDao(): FavoriteDao
}
