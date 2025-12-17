package com.tofiq.mvi_imdb.domain.model

import androidx.compose.runtime.Stable

/**
 * Movie categories for filtering.
 * Marked as @Stable since enums are inherently stable.
 */
@Stable
enum class Category(val displayName: String) {
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming"),
    NOW_PLAYING("Now Playing")
}
