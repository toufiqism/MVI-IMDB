package com.tofiq.mvi_imdb.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tofiq.mvi_imdb.presentation.screens.detail.DetailScreen
import com.tofiq.mvi_imdb.presentation.screens.favorites.FavoritesScreen
import com.tofiq.mvi_imdb.presentation.screens.home.HomeScreen
import com.tofiq.mvi_imdb.presentation.screens.search.SearchScreen

/**
 * Main navigation composable for the app using Navigation3.
 * 
 * Requirements: 7.1
 * - Uses animated transitions between screens
 * - Provides smooth navigation experience
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey> = rememberNavBackStack(HomeRoute),
    onNavigate: (NavRoute) -> Unit = { backStack.add(it) },
    onBack: () -> Unit = { backStack.removeLastOrNull() }
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    onMovieClick = { movieId ->
                        onNavigate(DetailRoute(movieId))
                    }
                )
            }

            entry<SearchRoute> {
                SearchScreen(
                    onMovieClick = { movieId ->
                        onNavigate(DetailRoute(movieId))
                    }
                )
            }

            entry<FavoritesRoute> {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        onNavigate(DetailRoute(movieId))
                    }
                )
            }

            entry<DetailRoute> { route ->
                DetailScreen(
                    movieId = route.movieId,
                    onBackClick = onBack,
                    onMovieClick = { movieId ->
                        onNavigate(DetailRoute(movieId))
                    }
                )
            }
        }
    )
}
