package com.tofiq.mvi_imdb.presentation.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.MovieGrid

/**
 * Search screen with search bar and results.
 * 
 * Requirements: 3.1, 3.3, 3.4
 * - Displays a search input field with focus
 * - Displays matching movies in a list format
 * - Shows empty state message when no results found
 */
@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Request focus on search field when screen opens
    // Requirements: 3.1 - Search input field with focus
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        SearchBar(
            query = state.query,
            onQueryChange = { query ->
                viewModel.processIntent(SearchIntent.UpdateQuery(query))
            },
            onClear = {
                viewModel.processIntent(SearchIntent.ClearSearch)
            },
            onSearch = {
                keyboardController?.hide()
            },
            focusRequester = focusRequester,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        onRetry = {
                            // Re-trigger search with current query
                            viewModel.processIntent(SearchIntent.UpdateQuery(state.query))
                        }
                    )
                }
                state.isEmpty -> {
                    // Requirements: 3.4 - Empty state message when no results
                    EmptySearchState()
                }
                state.query.length < 2 && state.movies.isEmpty() -> {
                    // Initial state - show hint
                    SearchHint()
                }
                else -> {
                    // Requirements: 3.3 - Display matching movies in list format
                    MovieGrid(
                        movies = state.movies,
                        isLoadingMore = state.isLoadingMore,
                        onMovieClick = onMovieClick,
                        onLoadMore = { viewModel.processIntent(SearchIntent.LoadNextPage) }
                    )
                }
            }
        }
    }
}

/**
 * Search bar composable with text field and clear button.
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.focusRequester(focusRequester),
        placeholder = { Text("Search movies...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

/**
 * Empty search results state.
 * Requirements: 3.4 - Display empty state message when no results
 */
@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No movies found",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Initial search hint state.
 */
@Composable
private fun SearchHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Enter at least 2 characters to search",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
