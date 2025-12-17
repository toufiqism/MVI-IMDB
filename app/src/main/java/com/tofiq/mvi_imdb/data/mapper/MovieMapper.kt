package com.tofiq.mvi_imdb.data.mapper

import com.tofiq.mvi_imdb.data.remote.dto.CastDto
import com.tofiq.mvi_imdb.data.remote.dto.MovieDetailDto
import com.tofiq.mvi_imdb.data.remote.dto.MovieDto
import com.tofiq.mvi_imdb.domain.model.Cast
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate ?: "",
    voteAverage = voteAverage,
    overview = overview ?: ""
)

fun List<MovieDto>.toDomainList(): List<Movie> = map { it.toDomain() }

fun CastDto.toDomain(): Cast = Cast(
    id = id,
    name = name,
    character = character ?: "",
    profilePath = profilePath
)

fun List<CastDto>.toCastList(): List<Cast> = map { it.toDomain() }

fun MovieDetailDto.toDomain(
    cast: List<Cast> = emptyList(),
    similarMovies: List<Movie> = emptyList(),
    isFavorite: Boolean = false
): MovieDetail = MovieDetail(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate ?: "",
    runtime = runtime,
    genres = genres?.map { it.name } ?: emptyList(),
    voteAverage = voteAverage,
    voteCount = voteCount,
    overview = overview ?: "",
    cast = cast,
    similarMovies = similarMovies,
    isFavorite = isFavorite
)
