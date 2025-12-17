package com.tofiq.mvi_imdb.data.mapper

import com.tofiq.mvi_imdb.data.local.entity.FavoriteEntity
import com.tofiq.mvi_imdb.data.local.entity.MovieEntity
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie

// Movie <-> MovieEntity
fun Movie.toEntity(category: Category, page: Int): MovieEntity = MovieEntity(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    overview = overview,
    category = category.name,
    page = page
)

fun MovieEntity.toDomain(isFavorite: Boolean = false): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    overview = overview,
    isFavorite = isFavorite,
    // Pre-compute values for recomposition optimization
    releaseYear = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "",
    formattedRating = String.format("%.1f", voteAverage)
)

fun List<MovieEntity>.toDomainList(favoriteIds: Set<Int> = emptySet()): List<Movie> =
    map { it.toDomain(isFavorite = it.id in favoriteIds) }

// Movie <-> FavoriteEntity
fun Movie.toFavoriteEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    overview = overview
)

fun FavoriteEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    overview = overview,
    isFavorite = true,
    // Pre-compute values for recomposition optimization
    releaseYear = releaseDate.takeIf { it.length >= 4 }?.substring(0, 4) ?: "",
    formattedRating = String.format("%.1f", voteAverage)
)

fun List<FavoriteEntity>.toFavoriteDomainList(): List<Movie> = map { it.toDomain() }
