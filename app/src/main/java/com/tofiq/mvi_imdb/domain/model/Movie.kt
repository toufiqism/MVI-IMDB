package com.tofiq.mvi_imdb.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val overview: String,
    val isFavorite: Boolean = false
) {
    val releaseYear: String
        get() = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: ""
    
    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
}
