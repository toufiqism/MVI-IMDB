package com.tofiq.mvi_imdb.presentation.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.tofiq.mvi_imdb.domain.model.Cast
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail
import com.tofiq.mvi_imdb.presentation.base.CollectEffect
import com.tofiq.mvi_imdb.presentation.components.CompactMovieCard
import com.tofiq.mvi_imdb.presentation.components.ErrorView
import com.tofiq.mvi_imdb.presentation.components.LoadingIndicator
import com.tofiq.mvi_imdb.presentation.components.ShimmerBox
import com.tofiq.mvi_imdb.presentation.components.ShimmerDetailScreen
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.RatingStar
import com.tofiq.mvi_imdb.ui.theme.VibrantPink
import com.tofiq.mvi_imdb.ui.theme.VibrantRed
import com.tofiq.mvi_imdb.util.Constants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enhanced detail screen with professional animations and effects.
 * 
 * Features:
 * - Parallax backdrop scrolling
 * - Animated section entrances
 * - Pulsing favorite button
 * - Staggered cast and similar movies animations
 * 
 * Requirements: 4.2, 4.3, 4.4, 5.1, 1.1 (cast-movies), 3.1, 3.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onCastClick: (personId: Int, personName: String, profilePath: String?) -> Unit = { _, _, _ -> },
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Load movie detail when movieId changes
    LaunchedEffect(movieId) {
        viewModel.processIntent(DetailIntent.LoadDetail(movieId))
    }
    
    // Collect effects for navigation and messages
    CollectEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is DetailEffect.NavigateToMovie -> onMovieClick(effect.movieId)
            is DetailEffect.NavigateToCastMovies -> onCastClick(
                effect.personId,
                effect.personName,
                effect.profilePath
            )
            is DetailEffect.ShowMessage -> {
                scope.launch {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
            is DetailEffect.NavigateBack -> onBackClick()
        }
    }

    // Calculate parallax offset based on scroll
    val parallaxOffset = scrollState.value * 0.5f

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    state.movieDetail?.let { detail ->
                        AnimatedFavoriteButton(
                            isFavorite = detail.isFavorite,
                            onClick = { viewModel.processIntent(DetailIntent.ToggleFavorite) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
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
                    ShimmerDetailScreen()
                }
                state.error != null -> {
                    ErrorView(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.processIntent(DetailIntent.Retry) }
                    )
                }
                state.movieDetail != null -> {
                    DetailContent(
                        movieDetail = state.movieDetail!!,
                        scrollState = scrollState,
                        parallaxOffset = parallaxOffset,
                        onMovieClick = { movieId -> 
                            viewModel.processIntent(DetailIntent.SimilarMovieClicked(movieId))
                        },
                        onCastClick = { personId, personName, profilePath ->
                            viewModel.processIntent(DetailIntent.CastClicked(personId, personName, profilePath))
                        }
                    )
                }
            }
        }
    }
}

/**
 * Animated favorite button with pulse effect.
 */
@Composable
private fun AnimatedFavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 300f
        ),
        label = "favoriteScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isFavorite) 360f else 0f,
        animationSpec = tween(AnimationSpecs.MEDIUM_DURATION),
        label = "favoriteRotation"
    )
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Filled.FavoriteBorder
            },
            contentDescription = if (isFavorite) {
                "Remove from favorites"
            } else {
                "Add to favorites"
            },
            tint = if (isFavorite) VibrantRed else Color.White,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
        )
    }
}

@Composable
private fun DetailContent(
    movieDetail: MovieDetail,
    scrollState: androidx.compose.foundation.ScrollState,
    parallaxOffset: Float,
    onMovieClick: (Int) -> Unit,
    onCastClick: (personId: Int, personName: String, profilePath: String?) -> Unit
) {
    var isContentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isContentVisible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Backdrop with parallax effect
        BackdropSection(
            movieDetail = movieDetail,
            parallaxOffset = parallaxOffset
        )
        
        // Movie info section with animated entrance
        AnimatedVisibility(
            visible = isContentVisible,
            enter = fadeIn(
                animationSpec = tween(AnimationSpecs.MEDIUM_DURATION)
            ) + slideInVertically(
                animationSpec = tween(AnimationSpecs.MEDIUM_DURATION)
            ) { it / 4 }
        ) {
            MovieInfoSection(movieDetail = movieDetail)
        }
        
        // Overview section
        AnimatedVisibility(
            visible = isContentVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = AnimationSpecs.MEDIUM_DURATION,
                    delayMillis = 100
                )
            ) + slideInVertically(
                animationSpec = tween(
                    durationMillis = AnimationSpecs.MEDIUM_DURATION,
                    delayMillis = 100
                )
            ) { it / 4 }
        ) {
            OverviewSection(overview = movieDetail.overview)
        }
        
        // Cast section
        if (movieDetail.cast.isNotEmpty()) {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 200
                    )
                ) + slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 200
                    )
                ) { it / 4 }
            ) {
                CastSection(
                    cast = movieDetail.cast,
                    onCastClick = onCastClick
                )
            }
        }
        
        // Similar movies section
        if (movieDetail.similarMovies.isNotEmpty()) {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 300
                    )
                ) + slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 300
                    )
                ) { it / 4 }
            ) {
                SimilarMoviesSection(
                    movies = movieDetail.similarMovies,
                    onMovieClick = onMovieClick
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BackdropSection(
    movieDetail: MovieDetail,
    parallaxOffset: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
    ) {
        // Backdrop image with parallax
        SubcomposeAsyncImage(
            model = Constants.getBackdropUrl(movieDetail.backdropPath),
            contentDescription = movieDetail.title,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = parallaxOffset * 0.3f
                },
            contentScale = ContentScale.Crop
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    ShimmerBox(modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp)
                }
                else -> SubcomposeAsyncImageContent()
            }
        }
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        
        // Title and basic info at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = movieDetail.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rating chip
                RatingChip(rating = movieDetail.formattedRating)
                
                // Year
                InfoChip(text = movieDetail.releaseYear)
                
                // Runtime
                movieDetail.runtime?.let {
                    InfoChip(text = movieDetail.formattedRuntime)
                }
            }
        }
    }
}

@Composable
private fun RatingChip(rating: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Rating",
            tint = RatingStar,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = rating,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun MovieInfoSection(movieDetail: MovieDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Genres as chips
        if (movieDetail.genres.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movieDetail.genres.size) { index ->
                    Text(
                        text = movieDetail.genres[index],
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewSection(overview: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = overview,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
        )
    }
}

@Composable
private fun CastSection(
    cast: ImmutableList<Cast>,
    onCastClick: (personId: Int, personName: String, profilePath: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Cast",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = cast.take(10),
                key = { _, it -> it.id },
                contentType = { _, _ -> "cast" }
            ) { index, castMember ->
                val onClickRemembered = remember(castMember.id) {
                    { onCastClick(castMember.id, castMember.name, castMember.profilePath) }
                }
                
                CastItem(
                    cast = castMember,
                    onClick = onClickRemembered,
                    index = index
                )
            }
        }
    }
}

@Composable
private fun CastItem(
    cast: Cast,
    onClick: () -> Unit,
    index: Int
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    var hasAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { hasAppeared = true }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = AnimationSpecs.SnappySpring,
        label = "castItemPress"
    )
    
    val entranceAlpha by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = index * AnimationSpecs.STAGGER_DELAY_FAST
        ),
        label = "castEntranceAlpha"
    )
    
    val entranceOffsetX by animateFloatAsState(
        targetValue = if (hasAppeared) 0f else 30f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = index * AnimationSpecs.STAGGER_DELAY_FAST
        ),
        label = "castEntranceOffset"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(90.dp)
            .graphicsLayer {
                alpha = entranceAlpha
                scaleX = scale
                scaleY = scale
                translationX = entranceOffsetX
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image
            SubcomposeAsyncImage(
                model = Constants.getProfileUrl(cast.profilePath),
                contentDescription = cast.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape),
                contentScale = ContentScale.Crop
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        ShimmerBox(modifier = Modifier.fillMaxSize(), cornerRadius = 40.dp)
                    }
                    is AsyncImagePainter.State.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cast.name.take(1),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> SubcomposeAsyncImageContent()
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Actor name
            Text(
                text = cast.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Character name
            Text(
                text = cast.character,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SimilarMoviesSection(
    movies: ImmutableList<Movie>,
    onMovieClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Similar Movies",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = movies.take(10),
                key = { _, it -> it.id },
                contentType = { _, _ -> "similar_movie" }
            ) { index, movie ->
                val onClickRemembered = remember(movie.id) {
                    { onMovieClick(movie.id) }
                }
                
                CompactMovieCard(
                    movie = movie,
                    onClick = onClickRemembered,
                    index = index
                )
            }
        }
    }
}
