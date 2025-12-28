package com.tofiq.mvi_imdb.presentation.screens.castmovies

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.tofiq.mvi_imdb.domain.model.CastMovie
import com.tofiq.mvi_imdb.presentation.base.CollectEffect
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.ShimmerBox
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.VibrantBlue
import com.tofiq.mvi_imdb.ui.theme.VibrantPurple
import com.tofiq.mvi_imdb.util.Constants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

/**
 * Enhanced Cast Movies screen with professional animations.
 * 
 * Features:
 * - Animated actor header with profile image
 * - Staggered grid item entrance
 * - Enhanced movie cards with character roles
 * 
 * Requirements: 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 4.1, 4.2, 4.3
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
    
    var isScreenVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isScreenVisible = true
    }
    
    // Collect effects
    CollectEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is CastMoviesEffect.NavigateToMovieDetail -> onMovieClick(effect.movieId)
            is CastMoviesEffect.ShowError -> { }
        }
    }
    
    // Load cast movies
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
                    AnimatedActorHeader(
                        personName = state.personName.ifEmpty { personName },
                        profilePath = state.profilePath ?: profilePath,
                        isVisible = isScreenVisible
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                    AnimatedEmptyMoviesView()
                }
                else -> {
                    CastMoviesGrid(
                        movies = state.movies,
                        onMovieClick = { movieId ->
                            viewModel.processIntent(CastMoviesIntent.MovieClicked(movieId))
                        }
                    )
                }
            }
        }
    }
}

/**
 * Animated actor header with profile image.
 */
@Composable
private fun AnimatedActorHeader(
    personName: String,
    profilePath: String?,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val profileUrl = remember(profilePath) {
        Constants.getProfileUrl(profilePath)
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = AnimationSpecs.BouncySpring,
        label = "headerScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(AnimationSpecs.MEDIUM_DURATION),
        label = "headerAlpha"
    )
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
    ) {
        // Profile photo
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (profileUrl != null) {
                SubcomposeAsyncImage(
                    model = profileUrl,
                    contentDescription = personName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            ShimmerBox(modifier = Modifier.fillMaxSize(), cornerRadius = 22.dp)
                        }
                        is AsyncImagePainter.State.Error -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "No profile photo",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = personName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Filmography",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Cast movies grid with staggered animations.
 */
@Composable
private fun CastMoviesGrid(
    movies: ImmutableList<CastMovie>,
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
            items = movies,
            key = { _, movie -> movie.id },
            contentType = { _, _ -> "cast_movie" }
        ) { index, movie ->
            val onClickRemembered = remember(movie.id) {
                { onMovieClick(movie.id) }
            }
            EnhancedCastMovieCard(
                movie = movie,
                onClick = onClickRemembered,
                index = index
            )
        }
    }
}

/**
 * Enhanced cast movie card with animations.
 */
@Composable
private fun EnhancedCastMovieCard(
    movie: CastMovie,
    onClick: () -> Unit,
    index: Int,
    modifier: Modifier = Modifier
) {
    val posterUrl = remember(movie.posterPath) {
        Constants.getPosterUrl(movie.posterPath)
    }
    
    var hasAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { hasAppeared = true }
    
    val staggerDelay = (index * AnimationSpecs.STAGGER_DELAY_FAST).coerceAtMost(400)
    
    val entranceAlpha by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = staggerDelay
        ),
        label = "cardAlpha"
    )
    
    val entranceScale by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0.85f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = staggerDelay
        ),
        label = "cardScale"
    )
    
    val entranceOffset by animateFloatAsState(
        targetValue = if (hasAppeared) 0f else 30f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = staggerDelay
        ),
        label = "cardOffset"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = entranceAlpha
                scaleX = entranceScale
                scaleY = entranceScale
                translationY = entranceOffset
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Movie poster
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            ) {
                if (posterUrl != null) {
                    SubcomposeAsyncImage(
                        model = posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                ShimmerBox(modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp)
                            }
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Movie,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )
            }
            
            // Movie info
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .height(72.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (movie.releaseYear.isNotEmpty()) {
                    Text(
                        text = movie.releaseYear,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                movie.character?.takeIf { it.isNotBlank() }?.let { character ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "as $character",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Animated empty state for no movies.
 */
@Composable
private fun AnimatedEmptyMoviesView(
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + scaleIn(animationSpec = AnimationSpecs.BouncySpring)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VibrantBlue.copy(alpha = 0.2f),
                                    VibrantPurple.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Movie,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = VibrantBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "No movies found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "This actor has no movie credits available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
