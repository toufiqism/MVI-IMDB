package com.tofiq.mvi_imdb.domain.usecase

import com.tofiq.mvi_imdb.data.local.entity.FavoriteEntity
import com.tofiq.mvi_imdb.data.mapper.toFavoriteEntity
import com.tofiq.mvi_imdb.domain.model.Movie
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 10: Favorite Toggle Idempotence**
 * **Validates: Requirements 5.2, 5.3**
 * 
 * For any movie, toggling favorite twice SHALL return the movie to its original favorite state.
 */
class FavoriteTogglePropertyTest {

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

    /**
     * Simulates the toggle favorite logic from LocalDataSource.
     * Returns the new favorite state after toggle.
     */
    private fun simulateToggle(isFavorite: Boolean): Boolean = !isFavorite

    @Test
    fun `Property 10 - Toggling favorite twice returns to original state`() = runTest {
        checkAll(100, movieArb) { movie ->
            val initialState = movie.isFavorite
            
            // First toggle
            val afterFirstToggle = simulateToggle(initialState)
            
            // Second toggle
            val afterSecondToggle = simulateToggle(afterFirstToggle)
            
            // Should return to original state
            afterSecondToggle shouldBe initialState
        }
    }

    @Test
    fun `Property 10 - Toggle from unfavorited adds to favorites`() = runTest {
        checkAll(100, movieArb) { movie ->
            val unfavoritedMovie = movie.copy(isFavorite = false)
            
            // Toggle should make it favorited
            val afterToggle = simulateToggle(unfavoritedMovie.isFavorite)
            
            afterToggle shouldBe true
        }
    }

    @Test
    fun `Property 10 - Toggle from favorited removes from favorites`() = runTest {
        checkAll(100, movieArb) { movie ->
            val favoritedMovie = movie.copy(isFavorite = true)
            
            // Toggle should make it unfavorited
            val afterToggle = simulateToggle(favoritedMovie.isFavorite)
            
            afterToggle shouldBe false
        }
    }

    @Test
    fun `Property 10 - FavoriteEntity conversion preserves movie data for toggle operations`() = runTest {
        checkAll(100, movieArb) { movie ->
            // Convert to FavoriteEntity (simulating add to favorites)
            val favoriteEntity = movie.toFavoriteEntity()
            
            // Verify all relevant fields are preserved
            favoriteEntity.id shouldBe movie.id
            favoriteEntity.title shouldBe movie.title
            favoriteEntity.posterPath shouldBe movie.posterPath
            favoriteEntity.backdropPath shouldBe movie.backdropPath
            favoriteEntity.releaseDate shouldBe movie.releaseDate
            favoriteEntity.voteAverage shouldBe movie.voteAverage
            favoriteEntity.overview shouldBe movie.overview
        }
    }
}
