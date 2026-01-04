package com.tofiq.mvi_imdb.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

/**
 * Animation specifications for the app.
 * Provides consistent animation durations, easing curves, and animation utilities.
 * 
 * Optimized for:
 * - Recomposition stability with remember
 * - Configuration change survival with rememberSaveable where appropriate
 * - Performance with graphicsLayer for GPU-accelerated animations
 */
object AnimationSpecs {
    // Duration constants
    const val INSTANT = 100
    const val SHORT_DURATION = 200
    const val MEDIUM_DURATION = 400
    const val LONG_DURATION = 600
    const val EXTRA_LONG_DURATION = 800
    
    // Stagger delay constants
    const val STAGGER_DELAY_FAST = 30
    const val STAGGER_DELAY_MEDIUM = 50
    const val STAGGER_DELAY_SLOW = 80
    
    // Spring configurations
    val DefaultSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val GentleSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val BouncySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val SnappySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    val VeryBouncySpring = spring<Float>(
        dampingRatio = 0.4f,
        stiffness = Spring.StiffnessLow
    )
    
    // Easing functions
    val FastOutSlowIn = FastOutSlowInEasing
    val LinearOutSlowIn = LinearOutSlowInEasing
    val FastOutLinearIn = FastOutLinearInEasing
    val Linear = LinearEasing
}

/**
 * Animation modifier for fade in effect with optional delay.
 * Uses graphicsLayer for GPU-accelerated rendering.
 */
@Composable
fun Modifier.fadeInAnimation(
    visible: Boolean = true,
    durationMillis: Int = AnimationSpecs.MEDIUM_DURATION,
    delayMillis: Int = 0
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "fadeIn"
    )
    return this.graphicsLayer { this.alpha = alpha }
}

/**
 * Animation modifier for scale effect with spring physics.
 */
@Composable
fun Modifier.scaleAnimation(
    targetScale: Float = 1f
): Modifier {
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = AnimationSpecs.DefaultSpring,
        label = "scale"
    )
    return this.graphicsLayer { 
        scaleX = animatedScale
        scaleY = animatedScale
    }
}

/**
 * Enhanced card hover/press animation with scale and subtle rotation.
 * Uses graphicsLayer for performance.
 */
@Composable
fun Modifier.cardPressAnimation(
    isPressed: Boolean = false
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationSpecs.SnappySpring,
        label = "cardPress"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) -1f else 0f,
        animationSpec = AnimationSpecs.GentleSpring,
        label = "cardRotation"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        rotationZ = rotation
    }
}

/**
 * Shimmer animation for loading placeholders.
 * Creates a horizontal sweep effect.
 */
@Composable
fun Modifier.shimmerAnimation(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPosition"
    )
    
    this.graphicsLayer {
        translationX = shimmerPosition * size.width
    }
}

/**
 * Slide in from bottom animation.
 * Uses graphicsLayer for GPU acceleration.
 */
@Composable
fun Modifier.slideInFromBottom(
    visible: Boolean = true,
    offsetY: Float = 100f,
    delayMillis: Int = 0
): Modifier {
    val offset by animateFloatAsState(
        targetValue = if (visible) 0f else offsetY,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delayMillis,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "slideInBottom"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delayMillis,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "slideInAlpha"
    )
    return this.graphicsLayer {
        translationY = offset
        this.alpha = alpha
    }
}

/**
 * Slide in from right animation.
 */
@Composable
fun Modifier.slideInFromRight(
    visible: Boolean = true,
    offsetX: Float = 300f,
    delayMillis: Int = 0
): Modifier {
    val offset by animateFloatAsState(
        targetValue = if (visible) 0f else offsetX,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delayMillis,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "slideInRight"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delayMillis
        ),
        label = "slideInRightAlpha"
    )
    return this.graphicsLayer {
        translationX = offset
        this.alpha = alpha
    }
}

/**
 * Staggered list item animation with fade, scale, and slide.
 * Optimized for lazy lists with index-based delay.
 */
@Composable
fun Modifier.staggeredItemAnimation(
    index: Int,
    visible: Boolean = true,
    baseDelay: Int = AnimationSpecs.STAGGER_DELAY_MEDIUM,
    maxDelay: Int = 500
): Modifier {
    // Cap the delay to prevent excessive waiting for items far down the list
    val delay = (index * baseDelay).coerceAtMost(maxDelay)
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delay,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "staggerAlpha$index"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delay,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "staggerScale$index"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 30f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delay,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "staggerOffset$index"
    )
    
    return this.graphicsLayer {
        this.alpha = alpha
        scaleX = scale
        scaleY = scale
        translationY = offsetY
    }
}

/**
 * Parallax scroll animation modifier.
 * Apply to elements that should move at different speeds during scroll.
 */
@Composable
fun Modifier.parallaxAnimation(
    scrollOffset: Float,
    parallaxFactor: Float = 0.5f
): Modifier {
    return this.graphicsLayer {
        translationY = scrollOffset * parallaxFactor
    }
}

/**
 * Pulsing animation for attention-grabbing elements.
 */
@Composable
fun Modifier.pulseAnimation(
    enabled: Boolean = true,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f
): Modifier = composed {
    if (!enabled) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Floating animation for subtle vertical movement.
 */
@Composable
fun Modifier.floatingAnimation(
    enabled: Boolean = true,
    amplitude: Float = 8f
): Modifier = composed {
    if (!enabled) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offset by infiniteTransition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )
    
    this.graphicsLayer {
        translationY = offset
    }
}

/**
 * Rotation animation for loading spinners.
 */
@Composable
fun Modifier.rotatingAnimation(
    durationMillis: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )
    
    this.graphicsLayer {
        rotationZ = rotation
    }
}

/**
 * Bounce in animation for appearing elements.
 */
@Composable
fun Modifier.bounceInAnimation(
    visible: Boolean = true,
    delayMillis: Int = 0
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceIn"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.SHORT_DURATION,
            delayMillis = delayMillis
        ),
        label = "bounceInAlpha"
    )
    
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/**
 * Glowing animation for highlighted elements.
 */
@Composable
fun Modifier.glowAnimation(
    enabled: Boolean = true,
    minAlpha: Float = 0.6f,
    maxAlpha: Float = 1f
): Modifier = composed {
    if (!enabled) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    this.graphicsLayer {
        this.alpha = alpha
    }
}

/**
 * 3D tilt animation for cards based on press position.
 */
@Composable
fun Modifier.tiltAnimation(
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    cameraDistance: Float = 8f
): Modifier {
    val animatedRotationX by animateFloatAsState(
        targetValue = rotationX,
        animationSpec = AnimationSpecs.GentleSpring,
        label = "tiltX"
    )
    val animatedRotationY by animateFloatAsState(
        targetValue = rotationY,
        animationSpec = AnimationSpecs.GentleSpring,
        label = "tiltY"
    )
    
    return this.graphicsLayer {
        this.rotationX = animatedRotationX
        this.rotationY = animatedRotationY
        this.cameraDistance = cameraDistance * density
    }
}

/**
 * Shake animation for error feedback.
 */
@Composable
fun rememberShakeAnimation(): Animatable<Float, AnimationVector1D> {
    return remember { Animatable(0f) }
}

suspend fun Animatable<Float, AnimationVector1D>.shake() {
    for (i in 0 until 4) {
        animateTo(
            targetValue = if (i % 2 == 0) 10f else -10f,
            animationSpec = tween(50)
        )
    }
    animateTo(0f, animationSpec = tween(50))
}

/**
 * Entrance animation that combines multiple effects.
 */
@Composable
fun Modifier.entranceAnimation(
    visible: Boolean = true,
    delayMillis: Int = 0
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delayMillis,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "entranceAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "entranceScale"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = tween(
            durationMillis = AnimationSpecs.MEDIUM_DURATION,
            delayMillis = delayMillis,
            easing = AnimationSpecs.FastOutSlowIn
        ),
        label = "entranceOffset"
    )
    
    return this.graphicsLayer {
        this.alpha = alpha
        scaleX = scale
        scaleY = scale
        translationY = offsetY
    }
}
