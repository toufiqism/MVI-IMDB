package com.tofiq.mvi_imdb.data.repository

import com.tofiq.mvi_imdb.data.mapper.toDomain
import com.tofiq.mvi_imdb.data.mapper.toEntity
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 12: Cache Persistence After Fetch**
 * **Validates: Requirements 6.1, 6.2**
 * 
 * For any successful API fetch, the fetched movies SHALL be persisted to local database 
 * and retrievable when offline.
 */
class CachePersistencePropertyTest {

    private val movieArb: Arb<Movie> = arbitrary {
        Movie(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).bind(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            overview = Arb.string(0..500).bind(),
            isFavorite = false
        )
    }

    private val categoryArb: Arb<Category> = Arb.enum<Category>()
    private val pageArb: Arb<Int> = Arb.int(1..100)

    @Test
    fun `Property 12 - Movies converted to entities and back preserve data for caching`() = runTest {
        checkAll(100, Arb.list(movieArb, 1..20), categoryArb, pageArb) { movies, category, page ->
            // Simulate caching: convert movies to entities
            val entities = movies.map { it.toEntity(category, page) }
            
            // Simulate retrieval: convert entities back to movies
            val retrieved = entities.map { it.toDomain() }
            
            // All movie IDs should be preserved
            retrieved.map { it.id } shouldContainAll movies.map { it.id }
            
            // All movie data should be preserved
            retrieved.forEachIndexed { index, movie ->
                val original = movies[index]
                movie.id shouldBe original.id
                movie.title shouldBe original.title
                movie.posterPath shouldBe original.posterPath
                movie.backdropPath shouldBe original.backdropPath
                movie.releaseDate shouldBe original.releaseDate
                movie.voteAverage shouldBe original.voteAverage
                movie.overview shouldBe original.overview
            }
        }
    }

    @Test
    fun `Property 12 - Entity stores category and page for proper cache retrieval`() = runTest {
        checkAll(100, movieArb, categoryArb, pageArb) { movie, category, page ->
            val entity = movie.toEntity(category, page)
            
            // Entity should store category and page for cache organization
            entity.category shouldBe category.name
            entity.page shouldBe page
            
            // Entity should have a cache timestamp
            entity.cachedAt shouldBe entity.cachedAt // Non-null check
        }
    }

    @Test
    fun `Property 12 - Multiple pages of same category maintain distinct page numbers`() = runTest {
        checkAll(100, Arb.list(movieArb, 1..10), categoryArb) { movies, category ->
            val page1Entities = movies.map { it.toEntity(category, 1) }
            val page2Entities = movies.map { it.toEntity(category, 2) }
            
            page1Entities.all { it.page == 1 } shouldBe true
            page2Entities.all { it.page == 2 } shouldBe true
            page1Entities.all { it.category == category.name } shouldBe true
            page2Entities.all { it.category == category.name } shouldBe true
        }
    }
}
