package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.VibrantOrange
import com.tofiq.mvi_imdb.ui.theme.VibrantRed
import kotlinx.coroutines.delay

/**
 * Animated error view composable with bounce-in animation and retry button.
 * 
 * Features:
 * - Animated icon with attention-grabbing effect
 * - Staggered content entrance
 * - Animated retry button with gradient
 * 
 * Optimized for:
 * - Recomposition stability
 * - Smooth entrance animations
 * 
 * Requirements: 1.4 - IF the network request fails THEN the Movie_List_Screen 
 * SHALL display an error message with a retry option
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated error icon
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = 200f
                    )
                ) + fadeIn()
            ) {
                ErrorIcon()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error title
            AnimatedVisibility(
                visible = isVisible,
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
                ) { it / 2 }
            ) {
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Error message
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 200
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 200
                    )
                ) { it / 2 }
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Retry button
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationSpecs.MEDIUM_DURATION,
                        delayMillis = 300
                    )
                ) + scaleIn(
                    animationSpec = spring(
                        dampingRatio = 0.6f,
                        stiffness = 300f
                    )
                )
            ) {
                RetryButton(onClick = onRetry)
            }
        }
    }
}

/**
 * Animated error icon with pulsing background.
 */
@Composable
private fun ErrorIcon() {
    val pulseScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 100f
        ),
        label = "errorPulse"
    )
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VibrantRed.copy(alpha = 0.2f),
                        VibrantOrange.copy(alpha = 0.1f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(40.dp),
            tint = VibrantRed
        )
    }
}

/**
 * Animated retry button with gradient background.
 */
@Composable
private fun RetryButton(
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationSpecs.SnappySpring,
        label = "retryScale"
    )
    
    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Try Again",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Compact error view for inline use.
 */
@Composable
fun CompactErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "Error",
            tint = VibrantRed,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Retry")
        }
    }
}
