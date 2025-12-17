package com.tofiq.mvi_imdb.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app using Navigation3 type-safe navigation.
 * 
 * Requirements: 4.1, 7.1
 * - Enables navigation to movie detail screen when user taps on a movie
 * - Supports animated transitions between screens
 */

/**
 * Sealed interface for all navigation routes.
 * This enables type-safe navigation with Navigation3.
 */
sealed interface NavRoute : NavKey

/**
 * Home screen route - displays categorized movie lists.
 */
@Serializable
data object HomeRoute : NavRoute

/**
 * Search screen route - allows searching for movies by title.
 */
@Serializable
data object SearchRoute : NavRoute

/**
 * Favorites screen route - displays user's saved favorite movies.
 */
@Serializable
data object FavoritesRoute : NavRoute

/**
 * Detail screen route - displays comprehensive movie information.
 * 
 * @param movieId The ID of the movie to display details for
 */
@Serializable
data class DetailRoute(val movieId: Int) : NavRoute

/**
 * Cast movies screen route - displays all movies featuring a specific actor.
 * 
 * Requirements: 1.1
 * - Enables navigation to cast movies screen when user taps on a cast member
 * 
 * @param personId The TMDB person ID of the actor
 * @param personName The name of the actor to display in the header
 * @param profilePath The profile image path of the actor (nullable)
 */
@Serializable
data class CastMoviesRoute(
    val personId: Int,
    val personName: String,
    val profilePath: String? = null
) : NavRoute
