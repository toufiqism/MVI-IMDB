package com.tofiq.mvi_imdb.data.mapper

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 14: Domain-Entity Round-Trip**
 * **Validates: Requirements 10.2, 10.3, 10.4**
 * 
 * For any Movie domain model, converting to MovieEntity and back to Movie 
 * SHALL preserve all data fields accurately.
 */
class EntityMapperPropertyTest {

    private val movieArb: Arb<Movie> = arbitrary {
        Movie(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).bind(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            overview = Arb.string(0..500).bind(),
            isFavorite = Arb.boolean().bind()
        )
    }

    private val categoryArb: Arb<Category> = Arb.enum<Category>()
    private val pageArb: Arb<Int> = Arb.int(1..100)

    @Test
    fun `Property 14 - Movie to MovieEntity to Movie round-trip preserves all fields`() = runTest {
        checkAll(100, movieArb, categoryArb, pageArb) { movie, category, page ->
            // Convert Movie -> MovieEntity -> Movie
            val entity = movie.toEntity(category, page)
            val roundTripped = entity.toDomain(isFavorite = movie.isFavorite)

            // All fields should be preserved
            roundTripped.id shouldBe movie.id
            roundTripped.title shouldBe movie.title
            roundTripped.posterPath shouldBe movie.posterPath
            roundTripped.backdropPath shouldBe movie.backdropPath
            roundTripped.releaseDate shouldBe movie.releaseDate
            roundTripped.voteAverage shouldBe movie.voteAverage
            roundTripped.overview shouldBe movie.overview
            roundTripped.isFavorite shouldBe movie.isFavorite
        }
    }

    @Test
    fun `Property 14 - Movie to FavoriteEntity to Movie round-trip preserves all fields`() = runTest {
        checkAll(100, movieArb) { movie ->
            // Convert Movie -> FavoriteEntity -> Movie
            val favoriteEntity = movie.toFavoriteEntity()
            val roundTripped = favoriteEntity.toDomain()

            // All fields should be preserved (isFavorite is always true for favorites)
            roundTripped.id shouldBe movie.id
            roundTripped.title shouldBe movie.title
            roundTripped.posterPath shouldBe movie.posterPath
            roundTripped.backdropPath shouldBe movie.backdropPath
            roundTripped.releaseDate shouldBe movie.releaseDate
            roundTripped.voteAverage shouldBe movie.voteAverage
            roundTripped.overview shouldBe movie.overview
            roundTripped.isFavorite shouldBe true
        }
    }
}
