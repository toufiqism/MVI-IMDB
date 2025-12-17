package com.tofiq.mvi_imdb.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model for a movie in an actor's filmography.
 * Marked as @Immutable to help Compose compiler optimize recompositions.
 * 
 * Requirements: 4.1, 4.2
 * - Displays movie poster, title, and release year
 * - Shows the character name the actor played in each movie
 */
@Immutable
data class CastMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val character: String?,
    val voteAverage: Double,
    // Pre-computed value to avoid recomposition from computed properties
    val releaseYear: String = releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4) ?: ""
)
