package com.tofiq.mvi_imdb.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VibrantDarkColorScheme = darkColorScheme(
    primary = VibrantBlue,
    onPrimary = Color.White,
    primaryContainer = DarkBlue,
    onPrimaryContainer = Color.White,
    
    secondary = VibrantPurple,
    onSecondary = Color.White,
    secondaryContainer = DarkPurple,
    onSecondaryContainer = Color.White,
    
    tertiary = VibrantPink,
    onTertiary = Color.White,
    tertiaryContainer = DarkPink,
    onTertiaryContainer = Color.White,
    
    error = VibrantRed,
    onError = Color.White,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color.White,
    
    background = DarkBackground,
    onBackground = Color.White,
    
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF616161),
    
    inverseSurface = Color.White,
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = VibrantBlue
)

private val VibrantLightColorScheme = lightColorScheme(
    primary = VibrantBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = DarkBlue,
    
    secondary = VibrantPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),
    onSecondaryContainer = DarkPurple,
    
    tertiary = VibrantPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCE4EC),
    onTertiaryContainer = DarkPink,
    
    error = VibrantRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    
    background = LightBackground,
    onBackground = Color(0xFF1C1B1F),
    
    surface = LightSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF424242),
    
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD),
    
    inverseSurface = Color(0xFF1C1B1F),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF90CAF9)
)

@Composable
fun MVIIMDBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our vibrant theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> VibrantDarkColorScheme
        else -> VibrantLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}