package com.tofiq.mvi_imdb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tofiq.mvi_imdb.data.local.entity.CastMovieEntity

@Dao
interface CastMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastMovies(movies: List<CastMovieEntity>)

    @Query("SELECT * FROM cast_movies WHERE personId = :personId ORDER BY releaseDate DESC")
    suspend fun getCastMovies(personId: Int): List<CastMovieEntity>

    @Query("DELETE FROM cast_movies WHERE personId = :personId")
    suspend fun deleteCastMovies(personId: Int)

    @Query("DELETE FROM cast_movies WHERE timestamp < :cutoffTime")
    suspend fun deleteOldCache(cutoffTime: Long)
}
