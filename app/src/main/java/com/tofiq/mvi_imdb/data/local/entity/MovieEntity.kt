package com.tofiq.mvi_imdb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val overview: String,
    val category: String,
    val page: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
