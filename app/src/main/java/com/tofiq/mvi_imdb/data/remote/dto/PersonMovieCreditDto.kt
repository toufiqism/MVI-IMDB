package com.tofiq.mvi_imdb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PersonMovieCreditDto(
    val id: Int,
    val title: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val character: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    val overview: String?
)
