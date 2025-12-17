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
 * **Feature: tmdb-movie-app, Property 8: Clear Search Resets State**
 * **Validates: Requirements 3.5**
 * 
 * For any clear search action, the SearchState SHALL reset query to empty string, 
 * movies to empty list, and isEmpty to false.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelClearSearchPropertyTest {

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
    fun `Property 8 - Clear search resets query to empty string`() = runTest(testDispatcher) {
        checkAll(100, validQueryArb, Arb.list(movieArb, 1..10)) { query, mockMovies ->
            // Setup repository mock
            every { repository.searchMovies(query, 1) } returns flowOf(Resource.Success(mockMovies))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // First, perform a search
            viewModel.processIntent(SearchIntent.UpdateQuery(query))
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify search was performed
            viewModel.state.value.query shouldBe query
            viewModel.state.value.movies shouldBe mockMovies
            
            // Now clear the search
            viewModel.processIntent(SearchIntent.ClearSearch)
            advanceUntilIdle()
            
            // Verify state is reset
            val finalState = viewModel.state.value
            finalState.query shouldBe ""
        }
    }

    @Test
    fun `Property 8 - Clear search resets movies to empty list`() = runTest(testDispatcher) {
        checkAll(100, validQueryArb, Arb.list(movieArb, 1..10)) { query, mockMovies ->
            // Setup repository mock
            every { repository.searchMovies(query, 1) } returns flowOf(Resource.Success(mockMovies))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // First, perform a search
            viewModel.processIntent(SearchIntent.UpdateQuery(query))
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify movies were loaded
            viewModel.state.value.movies shouldBe mockMovies
            
            // Now clear the search
            viewModel.processIntent(SearchIntent.ClearSearch)
            advanceUntilIdle()
            
            // Verify movies are cleared
            val finalState = viewModel.state.value
            finalState.movies shouldBe emptyList()
        }
    }

    @Test
    fun `Property 8 - Clear search resets isEmpty to false`() = runTest(testDispatcher) {
        checkAll(100, validQueryArb) { query ->
            // Setup repository to return empty list (triggers isEmpty = true)
            every { repository.searchMovies(query, 1) } returns flowOf(Resource.Success(emptyList<Movie>()))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // First, perform a search that returns empty results
            viewModel.processIntent(SearchIntent.UpdateQuery(query))
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Verify isEmpty is true
            viewModel.state.value.isEmpty shouldBe true
            
            // Now clear the search
            viewModel.processIntent(SearchIntent.ClearSearch)
            advanceUntilIdle()
            
            // Verify isEmpty is reset to false
            val finalState = viewModel.state.value
            finalState.isEmpty shouldBe false
        }
    }

    @Test
    fun `Property 8 - Clear search returns state to initial`() = runTest(testDispatcher) {
        checkAll(100, validQueryArb, Arb.list(movieArb, 1..10)) { query, mockMovies ->
            // Setup repository mock
            every { repository.searchMovies(query, 1) } returns flowOf(Resource.Success(mockMovies))
            
            searchMoviesUseCase = SearchMoviesUseCase(repository)
            val viewModel = SearchViewModel(searchMoviesUseCase)
            
            // First, perform a search
            viewModel.processIntent(SearchIntent.UpdateQuery(query))
            advanceTimeBy(500)
            advanceUntilIdle()
            
            // Now clear the search
            viewModel.processIntent(SearchIntent.ClearSearch)
            advanceUntilIdle()
            
            // Verify state matches initial state
            val finalState = viewModel.state.value
            finalState shouldBe SearchState.Initial
        }
    }
}
