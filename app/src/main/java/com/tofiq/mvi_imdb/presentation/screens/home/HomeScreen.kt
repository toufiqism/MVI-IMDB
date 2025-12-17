package com.tofiq.mvi_imdb.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tofiq.mvi_imdb.presentation.components.CategoryTabs
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.MovieGrid

/**
 * Home screen displaying categorized movie lists.
 * 
 * Requirements: 1.1, 1.5, 2.1, 2.4
 * - Displays popular movies on launch
 * - Shows movie poster, title, release year, and rating
 * - Displays category tabs for Popular, Top Rated, Upcoming, Now Playing
 * - Visually indicates the active category tab
 */
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        // Category tabs
        CategoryTabs(
            selectedCategory = state.selectedCategory,
            onCategorySelected = { category ->
                viewModel.processIntent(HomeIntent.SelectCategory(category))
            }
        )

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.movies.isEmpty() -> {
                    LoadingIndicator()
                }
                state.error != null && state.movies.isEmpty() -> {
                    ErrorView(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.processIntent(HomeIntent.Retry) }
                    )
                }
                else -> {
                    MovieGrid(
                        movies = state.movies,
                        isLoadingMore = state.isLoadingMore,
                        onMovieClick = onMovieClick,
                        onLoadMore = { viewModel.processIntent(HomeIntent.LoadNextPage) }
                    )
                }
            }
        }
    }
}


