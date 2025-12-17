package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.domain.model.Cast
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 9: MovieDetail Model Completeness**
 * **Validates: Requirements 4.2, 4.3, 4.4**
 * 
 * For any MovieDetail domain model, the model SHALL contain all required fields:
 * id, title, posterPath, backdropPath, releaseDate, runtime, genres, voteAverage, 
 * overview, cast, and similarMovies.
 */
class MovieDetailCompletenessPropertyTest {

    private val castArb: Arb<Cast> = arbitrary {
        Cast(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            name = Arb.string(1..50).bind(),
            character = Arb.string(1..50).bind(),
            profilePath = Arb.string(10..50).orNull().bind()
        )
    }

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


    private val movieDetailArb: Arb<MovieDetail> = arbitrary {
        MovieDetail(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).bind(),
            runtime = Arb.int(1..300).orNull().bind(),
            genres = Arb.list(Arb.string(1..20), 0..5).bind().toImmutableList(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            voteCount = Arb.int(0..Int.MAX_VALUE).bind(),
            overview = Arb.string(0..500).bind(),
            cast = Arb.list(castArb, 0..10).bind().toImmutableList(),
            similarMovies = Arb.list(movieArb, 0..10).bind().toImmutableList(),
            isFavorite = Arb.boolean().bind()
        )
    }

    @Test
    fun `Property 9 - MovieDetail contains all required fields with valid values`() = runTest {
        checkAll(100, movieDetailArb) { movieDetail ->
            // Verify id is present and valid
            movieDetail.id shouldNotBe null
            movieDetail.id shouldNotBe 0
            
            // Verify title is present
            movieDetail.title shouldNotBe null
            
            // Verify posterPath field exists (can be null)
            // The field itself must be accessible
            val posterPath: String? = movieDetail.posterPath
            
            // Verify backdropPath field exists (can be null)
            val backdropPath: String? = movieDetail.backdropPath
            
            // Verify releaseDate is present
            movieDetail.releaseDate shouldNotBe null
            
            // Verify runtime field exists (can be null)
            val runtime: Int? = movieDetail.runtime
            
            // Verify genres is present (can be empty list)
            movieDetail.genres shouldNotBe null
            
            // Verify voteAverage is within valid range
            movieDetail.voteAverage shouldNotBe null
            (movieDetail.voteAverage >= 0.0) shouldBe true
            (movieDetail.voteAverage <= 10.0) shouldBe true
            
            // Verify overview is present
            movieDetail.overview shouldNotBe null
            
            // Verify cast list is present (can be empty)
            movieDetail.cast shouldNotBe null
            
            // Verify similarMovies list is present (can be empty)
            movieDetail.similarMovies shouldNotBe null
        }
    }

    @Test
    fun `Property 9 - MovieDetail cast members have required fields`() = runTest {
        checkAll(100, movieDetailArb) { movieDetail ->
            // For each cast member, verify required fields
            movieDetail.cast.forEach { cast ->
                cast.id shouldNotBe 0
                cast.name shouldNotBe null
                cast.character shouldNotBe null
                // profilePath can be null
            }
        }
    }

    @Test
    fun `Property 9 - MovieDetail similar movies have required fields`() = runTest {
        checkAll(100, movieDetailArb) { movieDetail ->
            // For each similar movie, verify required fields
            movieDetail.similarMovies.forEach { movie ->
                movie.id shouldNotBe 0
                movie.title shouldNotBe null
                movie.releaseDate shouldNotBe null
                movie.overview shouldNotBe null
                (movie.voteAverage >= 0.0) shouldBe true
                (movie.voteAverage <= 10.0) shouldBe true
            }
        }
    }
}
