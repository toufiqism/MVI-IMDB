package com.tofiq.mvi_imdb.domain.model

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
)
