package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.data.mapper.toDomain
import com.tofiq.mvi_imdb.data.mapper.toFavoriteEntity
import com.tofiq.mvi_imdb.domain.model.Movie
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 11: Favorites Persistence Round-Trip**
 * **Validates: Requirements 5.4, 5.5**
 * 
 * For any movie added to favorites, retrieving favorites SHALL include that movie 
 * with all fields preserved.
 */
class FavoritesPersistencePropertyTest {

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

    @Test
    fun `Property 11 - Movie to FavoriteEntity and back preserves all fields`() = runTest {
        checkAll(100, movieArb) { movie ->
            // Convert to FavoriteEntity (simulating persistence)
            val favoriteEntity = movie.toFavoriteEntity()
            
            // Convert back to Movie (simulating retrieval)
            val retrieved = favoriteEntity.toDomain()
            
            // All fields should be preserved
            retrieved.id shouldBe movie.id
            retrieved.title shouldBe movie.title
            retrieved.posterPath shouldBe movie.posterPath
            retrieved.backdropPath shouldBe movie.backdropPath
            retrieved.releaseDate shouldBe movie.releaseDate
            retrieved.voteAverage shouldBe movie.voteAverage
            retrieved.overview shouldBe movie.overview
            
            // Retrieved from favorites should always have isFavorite = true
            retrieved.isFavorite shouldBe true
        }
    }

    @Test
    fun `Property 11 - Multiple movies added to favorites are all retrievable`() = runTest {
        checkAll(100, Arb.list(movieArb, 1..20)) { movies ->
            // Ensure unique IDs for this test
            val uniqueMovies = movies.distinctBy { it.id }
            
            // Convert all to FavoriteEntities (simulating batch persistence)
            val favoriteEntities = uniqueMovies.map { it.toFavoriteEntity() }
            
            // Convert back to Movies (simulating retrieval)
            val retrieved = favoriteEntities.map { it.toDomain() }
            
            // All movie IDs should be present
            retrieved.map { it.id } shouldContainAll uniqueMovies.map { it.id }
            
            // All retrieved movies should be marked as favorites
            retrieved.all { it.isFavorite } shouldBe true
        }
    }

    @Test
    fun `Property 11 - FavoriteEntity preserves nullable fields correctly`() = runTest {
        checkAll(100, movieArb) { movie ->
            val favoriteEntity = movie.toFavoriteEntity()
            val retrieved = favoriteEntity.toDomain()
            
            // Nullable fields should be preserved exactly
            retrieved.posterPath shouldBe movie.posterPath
            retrieved.backdropPath shouldBe movie.backdropPath
        }
    }

    @Test
    fun `Property 11 - FavoriteEntity preserves numeric precision`() = runTest {
        checkAll(100, movieArb) { movie ->
            val favoriteEntity = movie.toFavoriteEntity()
            val retrieved = favoriteEntity.toDomain()
            
            // Vote average should be preserved with same precision
            retrieved.voteAverage shouldBe movie.voteAverage
        }
    }
}
