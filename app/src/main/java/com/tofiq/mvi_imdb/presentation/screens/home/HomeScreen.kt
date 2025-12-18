package com.tofiq.mvi_imdb.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.presentation.base.CollectEffect
import com.tofiq.mvi_imdb.presentation.components.CategoryTabs
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.MovieGrid

/**
 * Home screen displaying categorized movie lists with swipe-to-change tabs.
 * Optimized for recomposition with remembered callbacks.
 * 
 * Requirements: 1.1, 1.5, 2.1, 2.4
 * - Displays popular movies on launch
 * - Shows movie poster, title, release year, and rating
 * - Displays category tabs for Popular, Top Rated, Upcoming, Now Playing
 * - Visually indicates the active category tab
 * - Supports swipe gestures to change between categories
 */
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val categories = remember { Category.entries }
    
    // Collect effects for navigation and one-time events
    // Requirements: 3.1, 3.2 - Use LaunchedEffect with effect flow, process once without replay
    CollectEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is HomeEffect.NavigateToMovieDetail -> onMovieClick(effect.movieId)
            is HomeEffect.ShowError -> {
                // Error handling can be extended here (e.g., show snackbar)
            }
        }
    }
    
    // Pager state for swipe functionality
    val pagerState = rememberPagerState(
        initialPage = categories.indexOf(state.selectedCategory),
        pageCount = { categories.size }
    )

    // Sync pager with tab selection (when tab is clicked)
    LaunchedEffect(state.selectedCategory) {
        val targetPage = categories.indexOf(state.selectedCategory)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Sync tab selection with pager (when swiped)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val category = categories[page]
            if (category != state.selectedCategory) {
                viewModel.processIntent(HomeIntent.SelectCategory(category))
            }
        }
    }

    // Remember callbacks to prevent unnecessary recompositions
    val onCategorySelected = remember(viewModel) {
        { category: Category ->
            viewModel.processIntent(HomeIntent.SelectCategory(category))
        }
    }
    
    val onRetry = remember(viewModel) {
        { viewModel.processIntent(HomeIntent.Retry) }
    }
    
    val onLoadMore = remember(viewModel) {
        { viewModel.processIntent(HomeIntent.LoadNextPage) }
    }
    
    // Movie click now emits intent instead of direct callback
    val onMovieClicked = remember(viewModel) {
        { movieId: Int ->
            viewModel.processIntent(HomeIntent.MovieClicked(movieId))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category tabs
        CategoryTabs(
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )

        // Swipeable content pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            // Only show content for the current selected category
            if (page == categories.indexOf(state.selectedCategory)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoading && state.movies.isEmpty() -> {
                            LoadingIndicator()
                        }
                        state.error != null && state.movies.isEmpty() -> {
                            ErrorView(
                                message = state.error ?: "Unknown error",
                                onRetry = onRetry
                            )
                        }
                        else -> {
                            MovieGrid(
                                movies = state.movies,
                                isLoadingMore = state.isLoadingMore,
                                onMovieClick = onMovieClicked,
                                onLoadMore = onLoadMore
                            )
                        }
                    }
                }
            } else {
                // Show loading for pages being swiped to
                LoadingIndicator()
            }
        }
    }
}


