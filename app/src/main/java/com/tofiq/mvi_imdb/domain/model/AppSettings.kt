package com.tofiq.mvi_imdb.domain.model

/**
 * Domain model for app settings.
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val cardCount: Int = 20,
    val viewMode: ViewMode = ViewMode.GRID
)

/**
 * Theme mode options.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * View mode options for displaying movies.
 */
enum class ViewMode {
    GRID,
    LIST
}
