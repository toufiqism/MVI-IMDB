package com.tofiq.mvi_imdb.data.mapper

import com.tofiq.mvi_imdb.data.local.entity.CastMovieEntity
import com.tofiq.mvi_imdb.domain.model.CastMovie

fun CastMovie.toCastMovieEntity(personId: Int): CastMovieEntity {
    return CastMovieEntity(
        id = id,
        personId = personId,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        character = character,
        voteAverage = voteAverage
    )
}

fun CastMovieEntity.toCastMovie(): CastMovie {
    return CastMovie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        character = character,
        voteAverage = voteAverage
    )
}

fun List<CastMovieEntity>.toCastMovieList(): List<CastMovie> {
    return map { it.toCastMovie() }
}
