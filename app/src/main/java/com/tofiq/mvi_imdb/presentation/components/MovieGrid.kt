package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * Optimized movie grid composable with lazy loading and pagination support.
 * 
 * Features:
 * - Staggered entrance animations for items
 * - Smooth pagination with loading indicator
 * - Efficient recomposition with stable keys
 * - Optimized scroll performance
 * 
 * Optimized for:
 * - Recomposition stability with remembered callbacks and stable collections
 * - Efficient scroll with proper content types and keys
 * - Configuration change survival with rememberLazyGridState
 * 
 * Requirements: 1.2 - WHEN the user scrolls to the bottom of the movie list 
 * THEN the Movie_List_Screen SHALL load the next page of movies automatically
 */
@Composable
fun MovieGrid(
    movies: ImmutableList<Movie>,
    isLoadingMore: Boolean,
    onMovieClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(12.dp)
) {
    val gridState = rememberLazyGridState()
    
    // Detect when user scrolls near the end to trigger pagination
    // Using derivedStateOf for optimal recomposition
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 6 && totalItems > 0
        }
    }

    // Trigger pagination when near bottom
    LaunchedEffect(shouldLoadMore, isLoadingMore) {
        if (shouldLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = movies,
            key = { _, movie -> movie.id },
            contentType = { _, _ -> "movie" }
        ) { index, movie ->
            // Remember the click callback to prevent recomposition
            val onClickRemembered = remember(movie.id) {
                { onMovieClick(movie.id) }
            }
            
            MovieCard(
                movie = movie,
                onClick = onClickRemembered,
                index = index
            )
        }
        
        // Loading indicator at the bottom during pagination
        if (isLoadingMore) {
            item(
                key = "loading_indicator",
                contentType = "loading"
            ) {
                PaginationLoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * Animated pagination loading indicator.
 */
@Composable
private fun PaginationLoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            CompactLoadingIndicator(size = 36.dp)
        }
    }
}

/**
 * Movie grid with adaptive columns based on screen width.
 * Automatically adjusts column count for optimal display.
 */
@Composable
fun AdaptiveMovieGrid(
    movies: ImmutableList<Movie>,
    isLoadingMore: Boolean,
    onMovieClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    minItemWidth: Int = 150,
    contentPadding: PaddingValues = PaddingValues(12.dp)
) {
    val gridState = rememberLazyGridState()
    
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 6 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore, isLoadingMore) {
        if (shouldLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minItemWidth.dp),
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = movies,
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
        
        if (isLoadingMore) {
            item(
                key = "loading_indicator",
                contentType = "loading"
            ) {
                PaginationLoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * Empty grid placeholder shown when no movies are available.
 */
@Composable
fun EmptyMovieGrid(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
