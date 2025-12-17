package com.tofiq.mvi_imdb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CastDto(
    val id: Int,
    val name: String,
    val character: String?,
    @SerializedName("profile_path") val profilePath: String?,
    val order: Int?,
    @SerializedName("known_for_department") val knownForDepartment: String?
)
