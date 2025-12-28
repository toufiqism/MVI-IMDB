package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.RatingStar
import com.tofiq.mvi_imdb.util.Constants

/**
 * Enhanced movie card composable with professional animations and effects.
 * 
 * Features:
 * - Gradient overlay on poster for better text readability
 * - Press animation with scale, elevation, and subtle rotation
 * - Staggered entrance animation based on index
 * - Shimmer loading placeholder while image loads
 * - Rating badge with gradient background
 * 
 * Optimized for:
 * - Recomposition stability with remembered values
 * - GPU-accelerated animations via graphicsLayer
 * - Configuration change survival
 * 
 * Requirements: 1.5 - WHEN displaying a movie item THEN the Movie_List_Screen 
 * SHALL show the movie poster, title, release year, and rating
 */
@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    // Remember the poster URL to avoid recalculation
    val posterUrl = remember(movie.posterPath) {
        Constants.getPosterUrl(movie.posterPath)
    }
    
    // Animation states
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Entrance animation - only plays once when item appears
    var hasAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasAppeared = true
    }
    
    // Staggered entrance delay (capped for performance)
    val staggerDelay = (index * AnimationSpecs.STAGGER_DELAY_FAST).coerceAtMost(400)
    
    // Press animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationSpecs.SnappySpring,
        label = "cardScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(AnimationSpecs.SHORT_DURATION),
        label = "cardElevation"
    )
    
    val rotationZ by animateFloatAsState(
        targetValue = if (isPressed) -0.5f else 0f,
        animationSpec = AnimationSpecs.GentleSpring,
        label = "cardRotation"
    )
    
    // Entrance animation values
    val entranceAlpha by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = staggerDelay
        ),
        label = "entranceAlpha"
    )
    
    val entranceScale by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 200f
        ),
        label = "entranceScale"
    )
    
    val entranceOffsetY by animateFloatAsState(
        targetValue = if (hasAppeared) 0f else 30f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = staggerDelay
        ),
        label = "entranceOffset"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                // Apply entrance animation
                alpha = entranceAlpha
                scaleX = scale * entranceScale
                scaleY = scale * entranceScale
                translationY = entranceOffsetY
                this.rotationZ = rotationZ
                // GPU layer for performance
                shadowElevation = elevation.toPx()
            }
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Movie poster with gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            ) {
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
                            ShimmerBox(
                                modifier = Modifier.fillMaxSize(),
                                cornerRadius = 0.dp
                            )
                        }
                        is AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No Image",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
                
                // Bottom gradient overlay for better text readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
                
                // Rating badge
                RatingBadge(
                    rating = movie.formattedRating,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
            
            // Movie info section
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .height(52.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = movie.releaseYear,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Rating badge with gradient background and star icon.
 */
@Composable
private fun RatingBadge(
    rating: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Black.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = RatingStar,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = rating,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Movie card variant for horizontal lists (similar movies, recommendations).
 * Smaller size optimized for horizontal scrolling.
 */
@Composable
fun CompactMovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val posterUrl = remember(movie.posterPath) {
        Constants.getPosterUrl(movie.posterPath)
    }
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationSpecs.SnappySpring,
        label = "compactCardScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = tween(AnimationSpecs.SHORT_DURATION),
        label = "compactCardElevation"
    )
    
    // Staggered entrance
    var hasAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { hasAppeared = true }
    
    val entranceAlpha by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = index * AnimationSpecs.STAGGER_DELAY_FAST
        ),
        label = "compactEntranceAlpha"
    )
    
    val entranceOffsetX by animateFloatAsState(
        targetValue = if (hasAppeared) 0f else 50f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = index * AnimationSpecs.STAGGER_DELAY_FAST
        ),
        label = "compactEntranceOffset"
    )
    
    Card(
        modifier = modifier
            .width(140.dp)
            .graphicsLayer {
                alpha = entranceAlpha
                scaleX = scale
                scaleY = scale
                translationX = entranceOffsetX
                shadowElevation = elevation.toPx()
            }
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            ) {
                SubcomposeAsyncImage(
                    model = posterUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            ShimmerBox(modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp)
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
                
                // Rating badge
                RatingBadge(
                    rating = movie.formattedRating,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = movie.releaseYear,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
