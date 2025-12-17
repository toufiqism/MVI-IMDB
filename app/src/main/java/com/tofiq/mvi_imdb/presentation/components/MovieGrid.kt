package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.domain.model.Movie
import kotlinx.collections.immutable.ImmutableList

/**
 * Reusable movie grid composable with lazy loading and pagination support.
 * Optimized for recomposition with stable parameters and remembered callbacks.
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
    contentPadding: PaddingValues = PaddingValues(8.dp)
) {
    val gridState = rememberLazyGridState()
    
    // Detect when user scrolls near the end to trigger pagination
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
        columns = GridCells.Fixed(columns),
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = movies,
            key = { it.id },
            contentType = { "movie" }
        ) { movie ->
            // Remember the click callback to prevent recomposition
            val onClickRemembered = remember(movie.id) {
                { onMovieClick(movie.id) }
            }
            MovieCard(
                movie = movie,
                onClick = onClickRemembered
            )
        }
        
        // Loading indicator at the bottom during pagination
        if (isLoadingMore) {
            item(
                key = "loading_indicator",
                contentType = "loading"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
