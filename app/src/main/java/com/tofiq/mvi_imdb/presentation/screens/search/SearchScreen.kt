package com.tofiq.mvi_imdb.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tofiq.mvi_imdb.data.local.SettingsDataStore
import com.tofiq.mvi_imdb.domain.model.AppSettings
import com.tofiq.mvi_imdb.domain.model.ViewMode
import com.tofiq.mvi_imdb.presentation.base.CollectEffect
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.MovieGrid
import com.tofiq.mvi_imdb.presentation.components.MovieList
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.VibrantBlue
import com.tofiq.mvi_imdb.ui.theme.VibrantPurple
import kotlinx.coroutines.delay

/**
 * Enhanced search screen with animated search bar and results.
 * 
 * Features:
 * - Animated search bar with focus effects
 * - Animated empty and hint states
 * - Smooth content transitions
 * 
 * Optimized for:
 * - Configuration change survival
 * - Recomposition stability
 * 
 * Requirements: 3.1, 3.3, 3.4
 * - Displays a search input field with focus
 * - Displays matching movies in a list format
 * - Shows empty state message when no results found
 */
@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    settingsDataStore: SettingsDataStore
) {
    val state by viewModel.state.collectAsState()
    val settings by settingsDataStore.settings.collectAsState(initial = AppSettings())
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var isScreenVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isScreenVisible = true
    }

    // Collect effects for navigation
    CollectEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is SearchEffect.NavigateToMovieDetail -> onMovieClick(effect.movieId)
        }
    }

    // Request focus on search field when screen opens
    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    // Remember callbacks
    val onQueryChange = remember(viewModel) {
        { query: String -> viewModel.processIntent(SearchIntent.UpdateQuery(query)) }
    }
    
    val onClear = remember(viewModel) {
        { viewModel.processIntent(SearchIntent.ClearSearch) }
    }
    
    val onSearch: () -> Unit = remember(keyboardController) {
        { keyboardController?.hide(); Unit }
    }
    
    val onLoadMore = remember(viewModel) {
        { viewModel.processIntent(SearchIntent.LoadNextPage) }
    }
    
    val onMovieClicked = remember(viewModel) {
        { movieId: Int ->
            viewModel.processIntent(SearchIntent.MovieClicked(movieId))
        }
    }

    // Screen entrance animation
    val screenAlpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(AnimationSpecs.MEDIUM_DURATION),
        label = "screenAlpha"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = screenAlpha }
    ) {
        // Animated search bar
        AnimatedVisibility(
            visible = isScreenVisible,
            enter = fadeIn() + slideInVertically { -it / 2 }
        ) {
            EnhancedSearchBar(
                query = state.query,
                onQueryChange = onQueryChange,
                onClear = onClear,
                onSearch = onSearch,
                focusRequester = focusRequester,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.movies.isEmpty() -> {
                    LoadingIndicator()
                }
                state.error != null && state.movies.isEmpty() -> {
                    val onRetry = remember(viewModel, state.query) {
                        { viewModel.processIntent(SearchIntent.UpdateQuery(state.query)) }
                    }
                    ErrorView(
                        message = state.error ?: "Unknown error",
                        onRetry = onRetry
                    )
                }
                state.isEmpty -> {
                    AnimatedEmptySearchState()
                }
                state.query.length < 2 && state.movies.isEmpty() -> {
                    AnimatedSearchHint()
                }
                else -> {
                    when (settings.viewMode) {
                        ViewMode.GRID -> {
                            MovieGrid(
                                movies = state.movies,
                                isLoadingMore = state.isLoadingMore,
                                onMovieClick = onMovieClicked,
                                onLoadMore = onLoadMore
                            )
                        }
                        ViewMode.LIST -> {
                            MovieList(
                                movies = state.movies,
                                isLoadingMore = state.isLoadingMore,
                                onMovieClick = onMovieClicked,
                                onLoadMore = onLoadMore
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enhanced search bar with gradient focus indicator.
 */
@Composable
private fun EnhancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val focusScale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = AnimationSpecs.GentleSpring,
        label = "searchBarScale"
    )
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .graphicsLayer {
                scaleX = focusScale
                scaleY = focusScale
            },
        placeholder = {
            Text(
                text = "Search movies...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isFocused) {
                    VibrantBlue
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VibrantBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

/**
 * Animated empty search results state.
 */
@Composable
private fun AnimatedEmptySearchState() {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + scaleIn(
                animationSpec = AnimationSpecs.BouncySpring
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Empty icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VibrantPurple.copy(alpha = 0.2f),
                                    VibrantBlue.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = VibrantPurple
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "No movies found",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Try a different search term",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Animated search hint state.
 */
@Composable
private fun AnimatedSearchHint() {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(AnimationSpecs.MEDIUM_DURATION)
            ) + slideInVertically(
                animationSpec = tween(AnimationSpecs.MEDIUM_DURATION)
            ) { it / 4 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                // Search icon with gradient background
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VibrantBlue.copy(alpha = 0.15f),
                                    VibrantPurple.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = VibrantBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Search for Movies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Enter at least 2 characters to start searching",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
