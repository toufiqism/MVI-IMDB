package com.tofiq.mvi_imdb.presentation.screens.home

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.GetMoviesUseCase
import com.tofiq.mvi_imdb.util.Resource
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
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 1: Pagination Preserves Existing Movies**
 * **Validates: Requirements 1.2**
 * 
 * For any movie list and pagination request, loading the next page SHALL append 
 * new movies to the existing list without removing or duplicating existing movies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelPaginationPropertyTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var getMoviesUseCase: GetMoviesUseCase

    private fun movieArb(idOffset: Int = 0): Arb<Movie> = arbitrary {
        Movie(
            id = Arb.int(1..100000).bind() + idOffset,
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).bind(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            overview = Arb.string(0..500).bind(),
            isFavorite = Arb.boolean().bind()
        )
    }
    
    // Helper to ensure unique IDs within a list
    private fun ensureUniqueIds(movies: List<Movie>): List<Movie> {
        val seen = mutableSetOf<Int>()
        return movies.mapIndexed { index, movie ->
            if (movie.id in seen) {
                movie.copy(id = movie.id + (index + 1) * 1000000)
            } else {
                seen.add(movie.id)
                movie
            }
        }
    }


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Property 1 - Pagination appends new movies without removing existing ones`() = runTest(testDispatcher) {
        checkAll(100, Arb.list(movieArb(0), 1..10), Arb.list(movieArb(500000), 1..10)) { rawFirstPage, rawSecondPage ->
            // Ensure unique IDs within each page and between pages
            val firstPageMovies = ensureUniqueIds(rawFirstPage)
            val firstPageIds = firstPageMovies.map { it.id }.toSet()
            val secondPageMovies = ensureUniqueIds(rawSecondPage).map { movie ->
                if (movie.id in firstPageIds) movie.copy(id = movie.id + 10000000) else movie
            }
            
            // Setup repository to return first page, then second page
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(firstPageMovies))
            every { repository.getMovies(Category.POPULAR, 2) } returns flowOf(Resource.Success(secondPageMovies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            // Wait for initial load
            advanceUntilIdle()
            
            // Verify first page loaded
            viewModel.state.value.movies shouldBe firstPageMovies
            viewModel.state.value.currentPage shouldBe 1
            
            // Load next page
            viewModel.processIntent(HomeIntent.LoadNextPage)
            advanceUntilIdle()
            
            // Verify pagination preserves existing movies and appends new ones
            val finalMovies = viewModel.state.value.movies
            finalMovies.size shouldBe firstPageMovies.size + secondPageMovies.size
            finalMovies shouldContainAll firstPageMovies
            finalMovies shouldContainAll secondPageMovies
            
            // Verify order: first page movies come before second page movies
            finalMovies.take(firstPageMovies.size) shouldBe firstPageMovies
            finalMovies.drop(firstPageMovies.size) shouldBe secondPageMovies
            
            viewModel.state.value.currentPage shouldBe 2
        }
    }

    @Test
    fun `Property 1 - Pagination does not duplicate movies`() = runTest(testDispatcher) {
        checkAll(100, Arb.list(movieArb(0), 1..10), Arb.list(movieArb(500000), 1..10)) { rawFirstPage, rawSecondPage ->
            // Ensure unique IDs within each page
            val firstPageMovies = ensureUniqueIds(rawFirstPage)
            val secondPageMovies = ensureUniqueIds(rawSecondPage)
            
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(firstPageMovies))
            every { repository.getMovies(Category.POPULAR, 2) } returns flowOf(Resource.Success(secondPageMovies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            viewModel.processIntent(HomeIntent.LoadNextPage)
            advanceUntilIdle()
            
            val finalMovies = viewModel.state.value.movies
            
            // Verify no duplicate IDs in final list - this is the core property
            // The ViewModel correctly deduplicates by ID when merging pages
            val finalIds = finalMovies.map { it.id }
            finalIds.distinct().size shouldBe finalIds.size
            
            // All first page movies should be present (they were loaded first)
            val firstPageIds = firstPageMovies.map { it.id }.toSet()
            finalMovies.filter { it.id in firstPageIds }.size shouldBe firstPageIds.size
        }
    }
}
