package com.tofiq.mvi_imdb.presentation.screens.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tofiq.mvi_imdb.presentation.base.CollectEffect
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.MovieCard
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.VibrantPink
import com.tofiq.mvi_imdb.ui.theme.VibrantRed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enhanced favorites screen with animated empty state and grid.
 * 
 * Features:
 * - Animated empty state with pulsing heart
 * - Staggered grid item entrance
 * - Smooth content transitions
 * 
 * Optimized for:
 * - Configuration change survival
 * - Recomposition stability
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
    
    var isScreenVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isScreenVisible = true
    }

    // Collect effects
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

    val onMovieClicked = remember(viewModel) {
        { movieId: Int ->
            viewModel.processIntent(FavoritesIntent.MovieClicked(movieId))
        }
    }

    // Screen entrance animation
    val screenAlpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(AnimationSpecs.MEDIUM_DURATION),
        label = "screenAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = screenAlpha }
    ) {
        when {
            state.isLoading -> {
                LoadingIndicator()
            }
            state.isEmpty -> {
                AnimatedEmptyFavoritesView()
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
 * Enhanced favorites grid with animations.
 */
@Composable
private fun FavoritesGrid(
    favorites: ImmutableList<com.tofiq.mvi_imdb.domain.model.Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = favorites,
            key = { _, movie -> movie.id },
            contentType = { _, _ -> "movie" }
        ) { index, movie ->
            val onClickRemembered = remember(movie.id) {
                { onMovieClick(movie.id) }
            }
            
            MovieCard(
                movie = movie,
                onClick = onClickRemembered,
                index = index
            )
        }
    }
}

/**
 * Animated empty favorites state with pulsing heart.
 */
@Composable
private fun AnimatedEmptyFavoritesView(
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    // Pulse animation for heart
    val heartScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = AnimationSpecs.VeryBouncySpring,
        label = "heartScale"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(AnimationSpecs.MEDIUM_DURATION)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                // Heart icon with gradient background
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = heartScale
                            scaleY = heartScale
                        }
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VibrantPink.copy(alpha = 0.2f),
                                    VibrantRed.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = VibrantPink
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "No favorites yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Movies you mark as favorite will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "❤️ Tap the heart on any movie to save it",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
