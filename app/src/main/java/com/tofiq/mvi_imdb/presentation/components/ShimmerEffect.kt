package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.ui.theme.ShimmerBaseDark
import com.tofiq.mvi_imdb.ui.theme.ShimmerBaseLight
import com.tofiq.mvi_imdb.ui.theme.ShimmerHighlightDark
import com.tofiq.mvi_imdb.ui.theme.ShimmerHighlightLight

/**
 * Shimmer effect modifier that creates a loading placeholder animation.
 * Uses GPU-accelerated graphics layer for smooth performance.
 * 
 * Optimized for:
 * - Recomposition stability with remember
 * - Smooth 60fps animation
 * - Dark/Light theme support
 */
@Composable
fun Modifier.shimmerEffect(
    isDarkTheme: Boolean = isSystemInDarkTheme()
): Modifier {
    val shimmerColors = remember(isDarkTheme) {
        if (isDarkTheme) {
            listOf(
                ShimmerBaseDark,
                ShimmerHighlightDark,
                ShimmerHighlightDark,
                ShimmerBaseDark
            )
        } else {
            listOf(
                ShimmerBaseLight,
                ShimmerHighlightLight,
                ShimmerHighlightLight,
                ShimmerBaseLight
            )
        }
    }
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    val brush = remember(translateAnimation) {
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnimation - 300f, translateAnimation - 300f),
            end = Offset(translateAnimation + 300f, translateAnimation + 300f)
        )
    }
    
    return this.background(brush)
}

/**
 * Shimmer placeholder box with rounded corners.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}

/**
 * Shimmer placeholder for a movie card.
 * Matches the exact dimensions of MovieCard for seamless loading transition.
 */
@Composable
fun ShimmerMovieCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Poster placeholder
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                cornerRadius = 0.dp
            )
            
            // Text placeholders
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .height(48.dp)
            ) {
                // Title placeholder
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp),
                    cornerRadius = 4.dp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Info placeholder
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp),
                    cornerRadius = 4.dp
                )
            }
        }
    }
}

/**
 * Shimmer grid for movie list loading state.
 * Displays multiple shimmer cards in a grid layout.
 */
@Composable
fun ShimmerMovieGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    itemCount: Int = 6,
    contentPadding: PaddingValues = PaddingValues(8.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {
        items(itemCount) {
            ShimmerMovieCard()
        }
    }
}

/**
 * Shimmer placeholder for horizontal movie list (similar movies, cast movies).
 */
@Composable
fun ShimmerHorizontalCard(
    modifier: Modifier = Modifier,
    width: Dp = 120.dp
) {
    Card(
        modifier = modifier
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Poster
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                cornerRadius = 0.dp
            )
            
            // Text
            Column(modifier = Modifier.padding(8.dp)) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(12.dp),
                    cornerRadius = 3.dp
                )
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(10.dp),
                    cornerRadius = 3.dp
                )
            }
        }
    }
}

/**
 * Shimmer for cast member circular avatars.
 */
@Composable
fun ShimmerCastItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        // Circular avatar
        ShimmerBox(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            cornerRadius = 40.dp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Name
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(10.dp),
            cornerRadius = 3.dp
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // Character
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(8.dp),
            cornerRadius = 3.dp
        )
    }
}

/**
 * Full detail screen shimmer placeholder.
 */
@Composable
fun ShimmerDetailScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Backdrop
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            cornerRadius = 0.dp
        )
        
        // Content
        Column(modifier = Modifier.padding(16.dp)) {
            // Genres
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp),
                cornerRadius = 4.dp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Overview title
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(18.dp),
                cornerRadius = 4.dp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Overview lines
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 3) 0.6f else 1f)
                        .height(14.dp),
                    cornerRadius = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cast title
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .height(18.dp),
                cornerRadius = 4.dp
            )
        }
    }
}




