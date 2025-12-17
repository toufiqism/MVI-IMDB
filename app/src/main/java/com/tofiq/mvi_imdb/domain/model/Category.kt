package com.tofiq.mvi_imdb.domain.model

enum class Category(val displayName: String) {
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming"),
    NOW_PLAYING("Now Playing")
}
