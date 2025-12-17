package com.tofiq.mvi_imdb.data.remote.dto

data class CreditsResponse(
    val id: Int,
    val cast: List<CastDto>,
    val crew: List<CrewDto>?
)

data class CrewDto(
    val id: Int,
    val name: String,
    val job: String?,
    val department: String?,
    @com.google.gson.annotations.SerializedName("profile_path") val profilePath: String?
)
