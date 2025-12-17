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
