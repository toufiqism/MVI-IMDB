package com.tofiq.mvi_imdb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    val overview: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    val popularity: Double?,
    @SerializedName("vote_count") val voteCount: Int?
)
