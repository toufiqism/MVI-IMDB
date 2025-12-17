package com.tofiq.mvi_imdb.data.mapper

import com.tofiq.mvi_imdb.data.remote.dto.CastDto
import com.tofiq.mvi_imdb.data.remote.dto.GenreDto
import com.tofiq.mvi_imdb.data.remote.dto.MovieDetailDto
import com.tofiq.mvi_imdb.data.remote.dto.MovieDto
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 15: DTO-Domain Mapping Completeness**
 * **Validates: Requirements 10.1**
 * 
 * For any valid MovieDto from API, mapping to Movie domain model 
 * SHALL preserve all non-null fields.
 */
class MovieMapperPropertyTest {

    private val movieDtoArb: Arb<MovieDto> = arbitrary {
        MovieDto(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).orNull().bind(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            overview = Arb.string(0..500).orNull().bind(),
            genreIds = null,
            popularity = null,
            voteCount = null
        )
    }

    private val castDtoArb: Arb<CastDto> = arbitrary {
        CastDto(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            name = Arb.string(1..50).bind(),
            character = Arb.string(0..50).orNull().bind(),
            profilePath = Arb.string(10..50).orNull().bind(),
            order = Arb.int(0..100).orNull().bind(),
            knownForDepartment = null
        )
    }

    private val genreDtoArb: Arb<GenreDto> = arbitrary {
        GenreDto(
            id = Arb.int(1..1000).bind(),
            name = Arb.string(1..30).bind()
        )
    }

    private val movieDetailDtoArb: Arb<MovieDetailDto> = arbitrary {
        MovieDetailDto(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).orNull().bind(),
            runtime = Arb.int(60..240).orNull().bind(),
            genres = Arb.list(genreDtoArb, 0..5).orNull().bind(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            voteCount = Arb.int(0..100000).bind(),
            overview = Arb.string(0..500).orNull().bind(),
            tagline = null,
            status = null,
            budget = null,
            revenue = null,
            productionCompanies = null
        )
    }

    @Test
    fun `Property 15 - MovieDto to Movie mapping preserves all non-null fields`() = runTest {
        checkAll(100, movieDtoArb) { dto ->
            val movie = dto.toDomain()

            // Required fields should always be preserved
            movie.id shouldBe dto.id
            movie.title shouldBe dto.title
            movie.posterPath shouldBe dto.posterPath
            movie.backdropPath shouldBe dto.backdropPath
            movie.voteAverage shouldBe dto.voteAverage

            // Nullable fields should be converted to empty string if null
            movie.releaseDate shouldBe (dto.releaseDate ?: "")
            movie.overview shouldBe (dto.overview ?: "")

            // Default value for isFavorite
            movie.isFavorite shouldBe false
        }
    }

    @Test
    fun `Property 15 - CastDto to Cast mapping preserves all non-null fields`() = runTest {
        checkAll(100, castDtoArb) { dto ->
            val cast = dto.toDomain()

            cast.id shouldBe dto.id
            cast.name shouldBe dto.name
            cast.character shouldBe (dto.character ?: "")
            cast.profilePath shouldBe dto.profilePath
        }
    }

    @Test
    fun `Property 15 - MovieDetailDto to MovieDetail mapping preserves all fields`() = runTest {
        checkAll(100, movieDetailDtoArb) { dto ->
            val detail = dto.toDomain()

            detail.id shouldBe dto.id
            detail.title shouldBe dto.title
            detail.posterPath shouldBe dto.posterPath
            detail.backdropPath shouldBe dto.backdropPath
            detail.releaseDate shouldBe (dto.releaseDate ?: "")
            detail.runtime shouldBe dto.runtime
            detail.genres shouldBe (dto.genres?.map { it.name } ?: emptyList())
            detail.voteAverage shouldBe dto.voteAverage
            detail.voteCount shouldBe dto.voteCount
            detail.overview shouldBe (dto.overview ?: "")
        }
    }

    @Test
    fun `Property 15 - List of MovieDto maps correctly`() = runTest {
        checkAll(100, Arb.list(movieDtoArb, 0..20)) { dtoList ->
            val movies = dtoList.toDomainList()

            movies.size shouldBe dtoList.size
            movies.forEachIndexed { index, movie ->
                movie.id shouldBe dtoList[index].id
                movie.title shouldBe dtoList[index].title
            }
        }
    }
}
