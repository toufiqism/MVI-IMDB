package com.tofiq.mvi_imdb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    val id: Int,
    val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val runtime: Int?,
    val genres: List<GenreDto>?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    val overview: String?,
    val tagline: String?,
    val status: String?,
    val budget: Long?,
    val revenue: Long?,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompanyDto>?
)

data class GenreDto(
    val id: Int,
    val name: String
)

data class ProductionCompanyDto(
    val id: Int,
    val name: String,
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("origin_country") val originCountry: String?
)
