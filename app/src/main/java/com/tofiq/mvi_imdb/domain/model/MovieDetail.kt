package com.tofiq.mvi_imdb.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val runtime: Int?,
    val genres: List<String>,
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String,
    val cast: List<Cast>,
    val similarMovies: List<Movie>,
    val isFavorite: Boolean = false
) {
    val releaseYear: String
        get() = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: ""
    
    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
    
    val formattedRuntime: String
        get() = runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A"
    
    val genresText: String
        get() = genres.joinToString(", ")
}
