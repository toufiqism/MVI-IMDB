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
import io.mockk.verify
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
 * **Feature: tmdb-movie-app, Property 6: Search Query Minimum Length**
 * **Validates: Requirements 3.2**
 * 
 * For any search query with fewer than 2 characters, the search operation 
 * SHALL NOT be triggered and the state SHALL remain unchanged.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelQueryMinLengthPropertyTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase

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

    // Generator for short queries (0-1 characters)
    private val shortQueryArb: Arb<String> = Arb.string(0..1)

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
    fun `Property 6 - Search query with fewer than 2 characters does not trigger search`() = runTest(testDispatcher) {
        checkAll(100, shortQueryArb, Arb.list(movieArb, 1..10)) { shortQuery, mockMovies ->
            // Setup repository mock - should NOT be called for short queries
            every { repository.searchMovies(any(), any()) } returns flowOf(Resource.Success(mockMovies))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // Get initial state
            val initialState = viewModel.state.value
            
            // Update query with short string
            viewModel.processIntent(SearchIntent.UpdateQuery(shortQuery))
            
            // Wait for debounce (300ms) and any potential processing
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify state remains unchanged (no movies loaded, no loading state)
            val finalState = viewModel.state.value
            finalState.movies shouldBe emptyList()
            finalState.isLoading shouldBe false
            finalState.isEmpty shouldBe false
            finalState.query shouldBe shortQuery
            
            // Verify repository was NOT called
            verify(exactly = 0) { repository.searchMovies(any(), any()) }
        }
    }

    @Test
    fun `Property 6 - Search query with 2 or more characters triggers search`() = runTest(testDispatcher) {
        // Generator for valid queries (2+ characters)
        val validQueryArb: Arb<String> = Arb.string(2..20)
        
        checkAll(100, validQueryArb, Arb.list(movieArb, 1..10)) { validQuery, mockMovies ->
            // Setup repository mock
            every { repository.searchMovies(validQuery, 1) } returns flowOf(Resource.Success(mockMovies))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // Update query with valid string
            viewModel.processIntent(SearchIntent.UpdateQuery(validQuery))
            
            // Wait for debounce (300ms) and processing
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify search was triggered and movies loaded
            val finalState = viewModel.state.value
            finalState.movies shouldBe mockMovies
            finalState.isLoading shouldBe false
            finalState.query shouldBe validQuery
            
            // Verify repository WAS called
            verify(atLeast = 1) { repository.searchMovies(validQuery, 1) }
        }
    }
}
