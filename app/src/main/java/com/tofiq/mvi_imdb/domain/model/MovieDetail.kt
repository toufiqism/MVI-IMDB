package com.tofiq.mvi_imdb.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Domain model for movie details.
 * Marked as @Immutable with pre-computed values to optimize recompositions.
 */
@Immutable
data class MovieDetail(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val runtime: Int?,
    val genres: ImmutableList<String> = persistentListOf(),
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String,
    val cast: ImmutableList<Cast> = persistentListOf(),
    val similarMovies: ImmutableList<Movie> = persistentListOf(),
    val isFavorite: Boolean = false,
    // Pre-computed values to avoid recomposition from computed properties
    val releaseYear: String = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "",
    val formattedRating: String = String.format("%.1f", voteAverage),
    val formattedRuntime: String = runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A",
    val genresText: String = genres.joinToString(", ")
) {
    companion object {
        /**
         * Factory function to create MovieDetail with regular lists converted to immutable.
         */
        fun create(
            id: Int,
            title: String,
            posterPath: String?,
            backdropPath: String?,
            releaseDate: String,
            runtime: Int?,
            genres: List<String>,
            voteAverage: Double,
            voteCount: Int,
            overview: String,
            cast: List<Cast>,
            similarMovies: List<Movie>,
            isFavorite: Boolean = false
        ): MovieDetail = MovieDetail(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            runtime = runtime,
            genres = genres.toImmutableList(),
            voteAverage = voteAverage,
            voteCount = voteCount,
            overview = overview,
            cast = cast.toImmutableList(),
            similarMovies = similarMovies.toImmutableList(),
            isFavorite = isFavorite,
            releaseYear = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "",
            formattedRating = String.format("%.1f", voteAverage),
            formattedRuntime = runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A",
            genresText = genres.joinToString(", ")
        )
    }
}
