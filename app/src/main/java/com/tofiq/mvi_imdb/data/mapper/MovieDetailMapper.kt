package com.tofiq.mvi_imdb.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tofiq.mvi_imdb.data.local.entity.MovieDetailEntity
import com.tofiq.mvi_imdb.data.remote.dto.MovieDetailDto
import com.tofiq.mvi_imdb.domain.model.Cast
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail

fun MovieDetailDto.toEntity(cast: List<Cast>, similarMovies: List<Movie>): MovieDetailEntity {
    val gson = Gson()
    val castJson = gson.toJson(cast)
    val similarMoviesJson = gson.toJson(similarMovies)
    val genresString = genres?.joinToString(",") { it.name } ?: ""

    return MovieDetailEntity(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        tagline = tagline,
        runtime = runtime,
        genres = genresString,
        cast = castJson,
        similarMovies = similarMoviesJson
    )
}

fun MovieDetailEntity.toDomain(isFavorite: Boolean): MovieDetail {
    val gson = Gson()
    val castType = object : TypeToken<List<Cast>>() {}.type
    val similarMoviesType = object : TypeToken<List<Movie>>() {}.type

    val castList: List<Cast> = gson.fromJson(cast, castType)
    val similarMoviesList: List<Movie> = gson.fromJson(similarMovies, similarMoviesType)
    val genreList = genres.split(",").toList()

    return MovieDetail.create(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        genres = genreList,
        cast = castList,
        similarMovies = similarMoviesList,
        isFavorite = isFavorite,
        tagline = tagline
    )
}
