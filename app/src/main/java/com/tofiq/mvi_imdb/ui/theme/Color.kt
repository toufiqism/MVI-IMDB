package com.tofiq.mvi_imdb.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// Primary Color Palette - Cinema-inspired colors
// ============================================================================

// Main brand colors
val CinemaRed = Color(0xFFE50914)         // Netflix-inspired red
val CinemaGold = Color(0xFFFFD700)        // Award gold
val CinemaBlue = Color(0xFF0D253F)        // Deep cinema blue

// Vibrant Color Palette - Light Theme
val VibrantBlue = Color(0xFF2196F3)       // Primary blue
val VibrantPurple = Color(0xFF9C27B0)     // Secondary purple
val VibrantPink = Color(0xFFE91E63)       // Tertiary pink
val VibrantOrange = Color(0xFFFF9800)     // Accent orange
val VibrantTeal = Color(0xFF00BCD4)       // Accent teal
val VibrantGreen = Color(0xFF4CAF50)      // Success green
val VibrantRed = Color(0xFFF44336)        // Error red
val VibrantYellow = Color(0xFFFFEB3B)     // Accent yellow
val VibrantCyan = Color(0xFF00E5FF)       // Neon cyan
val VibrantMagenta = Color(0xFFFF00FF)    // Neon magenta

// Dark Theme Colors - Deeper, richer variants
val DarkBlue = Color(0xFF1976D2)          // Darker blue for dark theme
val DarkPurple = Color(0xFF7B1FA2)        // Darker purple
val DarkPink = Color(0xFFC2185B)          // Darker pink
val DarkOrange = Color(0xFFF57C00)        // Darker orange
val DarkTeal = Color(0xFF0097A7)          // Darker teal

// ============================================================================
// Background & Surface Colors
// ============================================================================

// Light Theme Backgrounds
val LightBackground = Color(0xFFFFFBFE)
val LightSurface = Color(0xFFFFFBFE)
val LightSurfaceVariant = Color(0xFFF5F5F5)
val LightCard = Color(0xFFFFFFFF)

// Dark Theme Backgrounds - Rich blacks
val DarkBackground = Color(0xFF0A0A0A)    // Near black
val DarkSurface = Color(0xFF141414)       // Slightly lighter
val DarkSurfaceVariant = Color(0xFF1F1F1F)
val DarkCard = Color(0xFF1A1A1A)
val DarkElevated = Color(0xFF242424)

// ============================================================================
// Gradient Colors
// ============================================================================

// Primary gradients
val GradientStart = Color(0xFF667EEA)     // Soft blue
val GradientMiddle = Color(0xFF764BA2)    // Purple
val GradientEnd = Color(0xFFE91E63)       // Pink

// Cinema gradient - for premium feel
val CinemaGradientStart = Color(0xFF1A1A2E)
val CinemaGradientMiddle = Color(0xFF16213E)
val CinemaGradientEnd = Color(0xFF0F3460)

// Sunset gradient - warm tones
val SunsetStart = Color(0xFFFF512F)
val SunsetEnd = Color(0xFFDD2476)

// Ocean gradient - cool tones
val OceanStart = Color(0xFF2193B0)
val OceanEnd = Color(0xFF6DD5ED)

// Aurora gradient - magical
val AuroraStart = Color(0xFF00C9FF)
val AuroraMid = Color(0xFF92FE9D)
val AuroraEnd = Color(0xFF00C9FF)

// Dark glass gradient
val GlassGradientStart = Color(0x33FFFFFF)
val GlassGradientEnd = Color(0x0DFFFFFF)

// ============================================================================
// Shimmer Colors
// ============================================================================

val ShimmerBaseLight = Color(0xFFE0E0E0)
val ShimmerHighlightLight = Color(0xFFF5F5F5)
val ShimmerBaseDark = Color(0xFF2A2A2A)
val ShimmerHighlightDark = Color(0xFF3A3A3A)

// ============================================================================
// Rating Colors
// ============================================================================

val RatingGold = Color(0xFFFFD700)
val RatingHighlight = Color(0xFFFFC107)
val RatingStar = Color(0xFFFFB800)

// ============================================================================
// Status Colors
// ============================================================================

val SuccessGreen = Color(0xFF4CAF50)
val WarningAmber = Color(0xFFFFC107)
val ErrorRed = Color(0xFFE53935)
val InfoBlue = Color(0xFF2196F3)

// ============================================================================
// Legacy colors for backward compatibility
// ============================================================================

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ============================================================================
// Gradient Brush Helpers
// ============================================================================

/**
 * Pre-defined gradient brushes for consistent use across the app.
 * Using object to ensure single instance.
 */
object AppGradients {
    
    @Stable
    val primaryGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(VibrantBlue, VibrantPurple, VibrantPink)
        )
    
    @Stable
    val cinemaGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(CinemaGradientStart, CinemaGradientMiddle, CinemaGradientEnd)
        )
    
    @Stable
    val sunsetGradient: Brush
        @Composable
        get() = Brush.horizontalGradient(
            colors = listOf(SunsetStart, SunsetEnd)
        )
    
    @Stable
    val oceanGradient: Brush
        @Composable
        get() = Brush.horizontalGradient(
            colors = listOf(OceanStart, OceanEnd)
        )
    
    @Stable
    val glassGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(GlassGradientStart, GlassGradientEnd)
        )
    
    @Stable
    val cardOverlayGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.8f)
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    
    @Stable
    val backdropOverlayGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                Color.Black.copy(alpha = 0.6f),
                Color.Black.copy(alpha = 0.95f)
            )
        )
    
    @Stable
    fun shimmerGradient(isDark: Boolean): Brush {
        val (base, highlight) = if (isDark) {
            ShimmerBaseDark to ShimmerHighlightDark
        } else {
            ShimmerBaseLight to ShimmerHighlightLight
        }
        return Brush.horizontalGradient(
            colors = listOf(base, highlight, highlight, base)
        )
    }
    
    @Stable
    val ratingGradient: Brush
        @Composable
        get() = Brush.horizontalGradient(
            colors = listOf(RatingGold, RatingHighlight, RatingStar)
        )
    
    @Stable
    val favoriteGradient: Brush
        @Composable
        get() = Brush.radialGradient(
            colors = listOf(VibrantPink, VibrantRed)
        )
}
