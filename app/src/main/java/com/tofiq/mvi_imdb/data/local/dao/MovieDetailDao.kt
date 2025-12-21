package com.tofiq.mvi_imdb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tofiq.mvi_imdb.data.local.entity.MovieDetailEntity

@Dao
interface MovieDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetail(movieDetail: MovieDetailEntity)

    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    suspend fun getMovieDetailById(movieId: Int): MovieDetailEntity?

    @Query("DELETE FROM movie_details WHERE id = :movieId")
    suspend fun deleteMovieDetailById(movieId: Int)

    @Query("DELETE FROM movie_details WHERE timestamp < :cutoffTime")
    suspend fun deleteOldCache(cutoffTime: Long)
}
