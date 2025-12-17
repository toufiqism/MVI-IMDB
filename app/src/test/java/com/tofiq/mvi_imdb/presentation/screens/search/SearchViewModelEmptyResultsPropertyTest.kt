package com.tofiq.mvi_imdb.presentation.screens.search

import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.SearchMoviesUseCase
import com.tofiq.mvi_imdb.util.Resource
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
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 7: Empty Search Results State**
 * **Validates: Requirements 3.4**
 * 
 * For any search query that returns zero results, the SearchState SHALL have 
 * isEmpty=true and movies list SHALL be empty.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelEmptyResultsPropertyTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase

    // Generator for valid queries (2+ characters)
    private val validQueryArb: Arb<String> = Arb.string(2..20)

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
    fun `Property 7 - Empty search results sets isEmpty to true and movies to empty list`() = runTest(testDispatcher) {
        checkAll(100, validQueryArb) { query ->
            // Setup repository to return empty list (no results)
            every { repository.searchMovies(query, 1) } returns flowOf(Resource.Success(emptyList<Movie>()))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // Update query with valid string
            viewModel.processIntent(SearchIntent.UpdateQuery(query))
            
            // Wait for debounce (300ms) and processing
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify empty state is set correctly
            val finalState = viewModel.state.value
            finalState.isEmpty shouldBe true
            finalState.movies shouldBe emptyList()
            finalState.isLoading shouldBe false
            finalState.query shouldBe query
        }
    }

    @Test
    fun `Property 7 - Non-empty search results sets isEmpty to false`() = runTest(testDispatcher) {
        val movieArb: Arb<Movie> = arbitrary {
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
        
        checkAll(100, validQueryArb, movieArb) { query, movie ->
            // Setup repository to return non-empty list
            every { repository.searchMovies(query, 1) } returns flowOf(Resource.Success(listOf(movie)))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // Update query with valid string
            viewModel.processIntent(SearchIntent.UpdateQuery(query))
            
            // Wait for debounce (300ms) and processing
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify non-empty state
            val finalState = viewModel.state.value
            finalState.isEmpty shouldBe false
            finalState.movies.size shouldBe 1
            finalState.isLoading shouldBe false
        }
    }
}
