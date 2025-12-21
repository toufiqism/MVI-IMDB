package com.tofiq.mvi_imdb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cast_movies")
data class CastMovieEntity(
    @PrimaryKey val id: Int,
    val personId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val character: String?,
    val voteAverage: Double,
    val timestamp: Long = System.currentTimeMillis()
)
