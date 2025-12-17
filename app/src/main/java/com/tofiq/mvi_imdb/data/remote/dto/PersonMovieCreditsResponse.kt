package com.tofiq.mvi_imdb.data.remote.dto

data class PersonMovieCreditsResponse(
    val cast: List<PersonMovieCreditDto>,
    val crew: List<PersonMovieCreditDto>,
    val id: Int
)
