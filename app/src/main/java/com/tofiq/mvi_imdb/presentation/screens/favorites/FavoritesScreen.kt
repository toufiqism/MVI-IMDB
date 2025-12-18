package com.tofiq.mvi_imdb.presentation.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tofiq.mvi_imdb.presentation.base.CollectEffect
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.MovieCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

/**
 * Favorites screen displaying user's saved favorite movies.
 * 
 * Requirements: 5.4, 3.1, 3.2
 * - Displays all saved favorite movies from local database
 * - Shows movies in a grid layout
 * - Displays empty state when no favorites exist
 * - Collects effects for navigation and messages
 */
@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    // Collect effects for navigation and one-time events
    // Requirements: 3.1, 3.2 - Use LaunchedEffect with effect flow, process once without replay
    CollectEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is FavoritesEffect.NavigateToMovieDetail -> onMovieClick(effect.movieId)
            is FavoritesEffect.ShowFavoriteRemoved -> {
                scope.launch {
                    snackbarHostState.showSnackbar("${effect.movieTitle} removed from favorites")
                }
            }
        }
    }

    // Movie click now emits intent instead of direct callback
    val onMovieClicked = remember(viewModel) {
        { movieId: Int ->
            viewModel.processIntent(FavoritesIntent.MovieClicked(movieId))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                LoadingIndicator()
            }
            state.isEmpty -> {
                EmptyFavoritesView()
            }
            else -> {
                FavoritesGrid(
                    favorites = state.favorites,
                    onMovieClick = onMovieClicked
                )
            }
        }
    }
}

/**
 * Grid displaying favorite movies.
 * Optimized with remembered callbacks and stable keys.
 */
@Composable
private fun FavoritesGrid(
    favorites: ImmutableList<com.tofiq.mvi_imdb.domain.model.Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = favorites,
            key = { it.id },
            contentType = { "movie" }
        ) { movie ->
            val onClickRemembered = remember(movie.id) {
                { onMovieClick(movie.id) }
            }
            MovieCard(
                movie = movie,
                onClick = onClickRemembered
            )
        }
    }
}

/**
 * Empty state view when no favorites exist.
 */
@Composable
private fun EmptyFavoritesView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No favorites yet",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Movies you mark as favorite will appear here",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
