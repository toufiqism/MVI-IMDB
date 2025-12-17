package com.tofiq.mvi_imdb.presentation.screens.castmovies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tofiq.mvi_imdb.domain.model.CastMovie
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.util.Constants
import kotlinx.collections.immutable.ImmutableList


/**
 * Cast Movies screen displaying all movies featuring a specific actor.
 * 
 * Requirements: 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 4.1, 4.2, 4.3
 * - Display movies in a grid format consistent with other movie lists
 * - Display loading indicator during API request
 * - Display error message with retry option on failure
 * - Show actor's name in the app bar or header
 * - Show actor's profile photo if available
 * - Display placeholder image if profile photo unavailable
 * - Show movie poster, title, release year, and character name
 * - Display placeholder image for movies without poster
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastMoviesScreen(
    personId: Int,
    personName: String,
    profilePath: String?,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: CastMoviesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Load cast movies when screen is displayed
    LaunchedEffect(personId) {
        viewModel.processIntent(
            CastMoviesIntent.LoadCastMovies(
                personId = personId,
                personName = personName,
                profilePath = profilePath
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ActorHeader(
                        personName = state.personName.ifEmpty { personName },
                        profilePath = state.profilePath ?: profilePath
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    LoadingIndicator()
                }
                state.error != null -> {
                    ErrorView(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.processIntent(CastMoviesIntent.Retry) }
                    )
                }
                state.movies.isEmpty() -> {
                    EmptyMoviesView()
                }
                else -> {
                    CastMoviesGrid(
                        movies = state.movies,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

/**
 * Actor header displaying profile photo and name.
 * Requirements: 2.1, 2.2, 2.3
 */
@Composable
private fun ActorHeader(
    personName: String,
    profilePath: String?,
    modifier: Modifier = Modifier
) {
    val profileUrl = remember(profilePath) {
        Constants.getProfileUrl(profilePath)
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Profile photo or placeholder
        if (profileUrl != null) {
            AsyncImage(
                model = profileUrl,
                contentDescription = personName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder when profile photo unavailable (Requirement 2.3)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "No profile photo",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = personName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


/**
 * Grid displaying cast movies.
 * Requirements: 1.3, 4.1, 4.2, 4.3
 */
@Composable
private fun CastMoviesGrid(
    movies: ImmutableList<CastMovie>,
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
            items = movies,
            key = { it.id },
            contentType = { "cast_movie" }
        ) { movie ->
            val onClickRemembered = remember(movie.id) {
                { onMovieClick(movie.id) }
            }
            CastMovieCard(
                movie = movie,
                onClick = onClickRemembered
            )
        }
    }
}

/**
 * Card displaying a movie from an actor's filmography.
 * Requirements: 4.1, 4.2, 4.3
 * - Shows movie poster, title, and release year
 * - Shows character name the actor played
 * - Displays placeholder for movies without poster
 */
@Composable
private fun CastMovieCard(
    movie: CastMovie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posterUrl = remember(movie.posterPath) {
        Constants.getPosterUrl(movie.posterPath)
    }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Movie poster or placeholder (Requirement 4.3)
            if (posterUrl != null) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder when poster unavailable
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Movie info - title, year, and character
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .height(64.dp)
            ) {
                // Title (Requirement 4.1)
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Release year (Requirement 4.1)
                if (movie.releaseYear.isNotEmpty()) {
                    Text(
                        text = movie.releaseYear,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                
                // Character name (Requirement 4.2)
                movie.character?.takeIf { it.isNotBlank() }?.let { character ->
                    Text(
                        text = "as $character",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Empty state view when no movies are found.
 */
@Composable
private fun EmptyMoviesView(
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
                text = "No movies found",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "This actor has no movie credits available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
