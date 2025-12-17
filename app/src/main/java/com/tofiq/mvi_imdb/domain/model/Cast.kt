package com.tofiq.mvi_imdb.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model for a cast member.
 * Marked as @Immutable to help Compose compiler optimize recompositions.
 */
@Immutable
data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
)
