package com.tofiq.mvi_imdb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_details")
data class MovieDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val tagline: String?,
    val runtime: Int?,
    val genres: String, // Storing as a comma-separated string
    val cast: String, // Storing as a JSON string
    val similarMovies: String, // Storing as a JSON string
    val timestamp: Long = System.currentTimeMillis()
)
