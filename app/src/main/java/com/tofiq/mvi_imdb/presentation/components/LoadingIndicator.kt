package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.ui.theme.VibrantBlue
import com.tofiq.mvi_imdb.ui.theme.VibrantPink
import com.tofiq.mvi_imdb.ui.theme.VibrantPurple

/**
 * Professional loading indicator with cinema-themed animation.
 * Features a multi-ring spinner with gradient colors and pulsing effect.
 *
 * Optimized for:
 * - GPU-accelerated animations via graphicsLayer
 * - Smooth 60fps performance
 * - Configuration change stability
 *
 * Requirements: 1.3 - WHILE movies are loading THEN the Movie_List_Screen
 * SHALL display a loading indicator
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showText: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    // Outer ring rotation (clockwise)
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRotation"
    )

    // Inner ring rotation (counter-clockwise)
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerRotation"
    )

    // Pulse animation
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Alpha animation for breathing effect
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Colors for gradient
    val gradientColors = remember {
        listOf(VibrantBlue, VibrantPurple, VibrantPink, VibrantBlue)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                // Outer gradient ring
                Canvas(
                    modifier = Modifier
                        .size(size)
                        .graphicsLayer { rotationZ = outerRotation }
                ) {
                    val strokeWidth = size.toPx() * 0.1f
                    drawArc(
                        brush = Brush.sweepGradient(gradientColors),
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Middle ring
                Canvas(
                    modifier = Modifier
                        .size(size * 0.65f)
                        .graphicsLayer { rotationZ = innerRotation }
                ) {
                    val strokeWidth = size.toPx() * 0.08f
                    drawArc(
                        color = VibrantPurple,
                        startAngle = 45f,
                        sweepAngle = 200f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Inner pulsing dot
                Canvas(
                    modifier = Modifier.size(size * 0.3f)
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(VibrantPink, VibrantPurple.copy(alpha = 0.5f))
                        ),
                        radius = this.size.minDimension / 2
                    )
                }
            }

            if (showText) {
                Spacer(modifier = Modifier.height(16.dp))

                // Animated loading text
                LoadingText(alpha = alpha)
            }
        }
    }
}

/**
 * Loading text with animated dots.
 */
@Composable
private fun LoadingText(alpha: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.graphicsLayer { this.alpha = alpha }
    ) {
        Text(
            text = "Loading",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = ".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.graphicsLayer { this.alpha = dot1Alpha }
        )
        Text(
            text = ".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.graphicsLayer { this.alpha = dot2Alpha }
        )
        Text(
            text = ".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.graphicsLayer { this.alpha = dot3Alpha }
        )
    }
}

/**
 * Compact loading indicator for inline use (e.g., pagination).
 */
@Composable
fun CompactLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "compactLoading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "compactRotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compactScale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = scale
                    scaleY = scale
                },
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp,
            strokeCap = StrokeCap.Round
        )
    }
}

/**
 * Skeleton loading indicator that shows a pulsing placeholder.
 */
@Composable
fun SkeletonLoadingIndicator(
    modifier: Modifier = Modifier
) {
    ShimmerMovieGrid(
        modifier = modifier,
        itemCount = 6
    )
}
