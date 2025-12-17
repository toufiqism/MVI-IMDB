package com.tofiq.mvi_imdb.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model for a movie.
 * Marked as @Immutable to help Compose compiler optimize recompositions.
 */
@Immutable
data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val overview: String,
    val isFavorite: Boolean = false,
    // Pre-computed values to avoid recomposition from computed properties
    val releaseYear: String = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "",
    val formattedRating: String = String.format("%.1f", voteAverage)
)
